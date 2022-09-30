open Argu

open CodeLib.PatchManager
open crichton.injector

type CLIArguments =
    | [<Unique; Mandatory; AltCommandLine("-t") >] Target of filename:string
    | [<Unique; Mandatory; AltCommandLine("-o") >] Output of filename:string
    | [<Unique; AltCommandLine("-s") >] Spec of filename:string
with
    interface IArgParserTemplate with
        member s.Usage =
            match s with
            | Target _ -> "Target source code filename (.i/.ii)"
            | Output _ -> "Output filename"
            | Spec _   -> "Injection spec filename (.spec)"
            
[<EntryPoint>]
let main argv =
    printfn "Injector 1.0"
    
    let parser = ArgumentParser.Create<CLIArguments>(programName = "injector")
    
    if argv.Length < 1
    then
        parser.PrintUsage () |> printfn "%s"
        exit -1

    try
        // Processing options
        let results = parser.Parse argv
        
        let mutable target_name = ""
        let mutable output_name = ""
        let mutable spec_name = ""
        
        for opt in results.GetAllResults() do
            match opt with
            | Target x -> target_name <- x
            | Output x -> output_name <- x
            | Spec x   -> spec_name   <- x
            
        // Do main
        let file_patcher = FilePatcher target_name
        PatchApplier.doit file_patcher
        file_patcher.flush output_name <| Replacer Map.empty
        
    with 
    | :? ArguParseException as x ->
        printfn $"%s{x.Message}"
        
    0