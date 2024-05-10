open System
open System.Diagnostics
open System.Globalization
open System.IO
open Newtonsoft.Json
open Newtonsoft.Json.Linq
open SpecLib

type Task = {
    Name: string
    Count: int
    Time: int
}
type Watch = {
    Name: string
    Value: Common.ValueType
}
type State = {
    Processes: Task
    Watches: Map<string, Watch>
    Time: int
    Index: int
}

let parseValue (v:string) : Common.ValueType option =
   let tryParseInteger () =
        match Int32.TryParse(v) with
        | (true, result) -> Some (Common.Integer result)
        | _ -> None
   let tryParseFloat () =
        match Double.TryParse(v) with
        | (true, result) -> Some (Common.Float result)
        | _ -> None
   let tryParseHexadecimal () =
        match Int32.TryParse(v, NumberStyles.HexNumber, null) with
        | (true, result) -> Some (Common.Hex result)
        | _ -> None
   let tryParseChar () =
       match Char.TryParse(v) with
       | (true, result) -> Some (Common.Char result)
       | _ -> None
  
   tryParseInteger () 
   |> Option.orElse (tryParseFloat ())
   |> Option.orElse (tryParseHexadecimal ())
   |> Option.orElse (tryParseChar ())
   |> Option.orElse (Some (Common.Str v)): Common.ValueType option

let parseSafe (s: string) =
    let logs = s.Split("@{").[1..]
    let stat =
        logs
        |> Array.map (fun (log: string) ->
                let tmp = log.Split("@->")
                let var = tmp[0]
                let valString = if tmp.Length < 3 then
                                    String.concat "@->" tmp.[1..]
                                else
                                    tmp[1]
                let value  =
                    match parseValue(valString) with
                    | Some x -> x
                    | None -> Common.Integer 0
                (var, {Name = var; Value = value; })
                )
    stat

let parseLog (log: string) =
    let logs = log.Split("\n")
    let mutable t = {Name = ""; Count = 0; Time = 0;}
    let mutable s = [||]: State array
    for line in logs do
        if line.Contains("running...") then
            let sp = line.Split(" ")
            let nameSplit = sp[sp.Length-3].Split("_")
            let name = if sp[sp.Length-3].Contains("crichtonInject") then
                            sp[sp.Length-3]
                        else
                            String.concat "_" nameSplit.[2..]
                            
            t <- {Name = name; Count = int sp[sp.Length-2]; Time = int sp[sp.Length-1]}
            //t <- {Name = sp[sp.Length-3]; Count = int sp[sp.Length-2]; Time = int sp[sp.Length-1]}
        if line.Contains("[SafeMonitor]") then
            let w = Map.ofArray (parseSafe(line.Substring(14)))
            s <- Array.append s [|{Processes = t; Watches = w; Time = 0; Index = s.Length + 1}|]
    s
   

let monitor (execFile: string, args) =
    let processInfo = new ProcessStartInfo(execFile, args)
    let mutable otherText = ""
    let mutable printText = ""
    processInfo.RedirectStandardOutput <- true
    processInfo.UseShellExecute <- false

    let proc = new Process()
    proc.StartInfo <- processInfo
    
    proc.OutputDataReceived.Add(fun (x: DataReceivedEventArgs) ->
        if not (String.IsNullOrEmpty(x.Data)) then
            if (x.Data.Contains("@crichton: ")) then
                #if MONITOR
                printText <- printText + x.Data.Remove(0,11) + "\r\n"
                printf $"Logging: {x.Data.Remove(0,11)}\r\n"
                #else
                otherText <- otherText + x.Data.Remove(0,11) + "\r\n"
                #endif
            else
                #if MONITOR
                otherText <- otherText + x.Data + "\r\n"
                #else
                printText <- printText + x.Data + "\r\n"
                printf $"{x.Data}\r\n"
                #endif
    )
    proc.Start() |> ignore
    proc.BeginOutputReadLine()

    proc.WaitForExit()
    #if MONITOR
    let st = parseLog(printText)
    #else
    let st = parseLog(otherText)
    #endif
    st

let checkState (state: State) (safe: SafeLib.SafeItem) =
    match state.Watches.TryFind safe.Var with
    | Some x -> match Array.contains x.Value safe.Spec with
                   | true -> None
                   | false -> Some safe
    | None -> None
    
let checkSafeCount (item: SafeLib.SafeItem) (log: (State * SafeLib.SafeItem list) array ) (sw: StreamWriter) =
    let mutable acc = 0
    let mutable prev = -1
    let mutable isOver = false
    let safeName = match item.Limit with
                    | SafeLib.Cnt (x,y) -> y
                    | _ -> ""

    for (st,items) in log do
        if safeName = st.Processes.Name && List.contains item items then
            if prev = -1 || st.Processes.Count - 1 = prev then
                acc <- acc + 1
                match item.Limit with
                    | SafeLib.Cnt (x, name) -> if acc > x then isOver <- true
                    | _ -> ()
            else
                acc <- 1
            prev <- st.Processes.Count
        else if safeName = st.Processes.Name then
            acc <- 0
    if isOver then
        sw.WriteLine(item.Id.ToString() + ",\t" + item.Var)
        
let checkSafeDuring (item: SafeLib.SafeItem) (log: (State * SafeLib.SafeItem list) array ) (sw: StreamWriter) =
    let mutable prev = -1
    let mutable isOver = false
    let delta = match item.Limit with
                    | SafeLib.During x -> x
                    | _ -> 0
    for (st,items) in log do
        if List.contains item items then
            if prev = -1 then
                prev <- st.Processes.Time
            else
                if (st.Processes.Time - prev) > delta then
                    isOver <- true
                    ()
        else
            prev <- -1
    if isOver then
        sw.WriteLine(item.Id.ToString() + ",\t" + item.Var)
        
let checkSafeAny (item: SafeLib.SafeItem) (sw: StreamWriter) =
    sw.WriteLine(item.Id.ToString() + ",\t" + item.Var)
    
let genReport (state: State array) (safe: SafeLib.SafeItem list) (filename: string)=
    let temp = state |> Array.map (fun st ->
        let ret = safe |> List.map (fun item ->
            checkState st item ) |> List.filter Option.isSome
        (st,ret) ) |> Array.filter (fun (_,x) -> not (x = []))
    let ss = temp |> Array.map (fun (st,s) -> (st, List.choose (fun option -> option) s))
    let items = temp |> Array.fold (fun (acc:SafeLib.SafeItem list) (_,x:SafeLib.SafeItem Option list) ->
        List.append acc (List.choose (fun option -> option) x ) ) [] |> Set.ofList
    //Report Generation
    use sw = new StreamWriter(filename)
    sw.WriteLine("ID,\tViolationVarName")
    for item in items do
        match item.Limit with
        | SafeLib.Any _ -> checkSafeAny item sw
        | SafeLib.Cnt (x,_) -> checkSafeCount item ss sw
        | SafeLib.During x -> checkSafeDuring item ss sw

let genSafeList (filename: string) =
     use sr = new StreamReader(filename)
     let jsonArray =
        sr.ReadToEnd ()
        |> JsonConvert.DeserializeObject<JArray>
     let json = SafeLib.safeParser jsonArray
     json
     
let main =
    let args = System.Environment.GetCommandLineArgs()
    let (safe,out,filename,tArgs) = if args.Length < 4 then
                                        printfn "Bad Argument"
                                        printfn "Usage: InjectionTester <safe spec> <output> <target program> <args>"
                                        exit -1
                                        else
                                            (args[1], args[2], args[3], args.[4..])
    #if POSIX
    let arg = String.concat " " tArgs
    #else
    let arg = "/home/yeoneo/crichton/test/trampoline/examples/cortex-m/armv7em/stm32f407/stm32f4discovery/inject_val/run_stm32f4discovery.sh "+ filename + String.concat " " tARgs
    let filename = "sh"
    #endif
    let state = monitor (filename,arg)
    let safe = genSafeList safe
    genReport state safe out
    
    