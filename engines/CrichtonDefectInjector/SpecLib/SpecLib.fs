module SpecLib

open System.IO
open Newtonsoft.Json.Linq

module Common = 
    type ValueType =
        | Integer of int
        | Char of char
        | Float of float
        | Str of string
        | Hex of int
        | Unknown of string
        
module TestLib =
    type TaskDef = {
        Name: string
        Start: int
        Cycle: int
        Priority: int
        File: string
        Code: string
    }
    
    type TestDef = {
        Tasks: TaskDef list
        Stop: int
    }
    
    let testParser (obj: JObject):TestDef =
        let root = obj.Root
        let tasks = Seq.toList (root["tasks"].ToObject<JArray>().Children() |> Seq.map (fun (i: JToken) ->
            let item = i.ToObject<JObject>().Root
            let _name = item["name"].ToObject<string>()
            let _start = item["start"].ToObject<int>()
            let _cycle = item["cycle"].ToObject<int>()
            let _priority = item["priority"].ToObject<int>()
            let _file = item["name"].ToObject<string>()
            use codeTarget = new StreamReader(_file)
            let _code = codeTarget.ReadToEnd()
            {Name = _name; Start = _start; Cycle = _cycle; Priority = _priority; File = _file; Code = _code }
            
        ))
        let stop = root["stop"].ToObject<int>()
        {Tasks = tasks; Stop = stop }
        
module DefectLib =
    type DefectType =
        | Taint of kind:string * var:string * ty:string * value:Common.ValueType * pattern:string * code:string option
        | BitFlip of kind:string * var:string * ty:string * value:Common.ValueType * pattern:string * code:string option
        | Undef

    type DefectItem = {
        Id: int
        Target: string //target task name
        Trigger: int
        Repeat: int
        Defect: DefectType
    }

    type DefectModel = {
        id: int
        taskCode: string
        alarmCode: string
        taskOil: string
        alarmOil: string -> string
    }
    let defectParser (obj: JObject):DefectItem =
        let item = obj.Root
        let id = item["id"].ToObject<int>()
        let trigger = item["trigger"].ToObject<int>()
        let cycle = item["cycle"].ToObject<int>()
        let target = item["target"].ToObject<string>()
        let defect = match item["defect"].ToObject<JObject>().First.Path with
                                | "taint" -> let detail = item["defect"].["taint"]
                                             let _var = detail["var"].ToObject<string>()
                                             let _ty = detail["type"].ToObject<string>()
                                             let _val =  match detail["value"].Type with
                                                            | JTokenType.Integer -> Common.Integer (detail["value"].ToObject<int>())
                                                            | JTokenType.String -> Common.Str (detail["value"].ToObject<string>())
                                                            | JTokenType.Float -> Common.Float (detail["value"].ToObject<float>())
                                                            | _ -> Common.Unknown ""
                                             let _pattern = detail["pattern"].ToObject<string>()
                                             Taint ("taint", _var, _ty, _val, _pattern, None)
                                | "bitflip" ->  let detail = item["defect"].["bitflip"]
                                                let _var = detail["var"].ToObject<string>()
                                                let _ty = detail["type"].ToObject<string>()
                                                let _val =  match detail["value"].Type with
                                                                | JTokenType.Integer -> Common.Integer (detail["value"].ToObject<int>())
                                                                | JTokenType.String -> Common.Str (detail["value"].ToObject<string>())
                                                                | JTokenType.Float -> Common.Float (detail["value"].ToObject<float>())
                                                                | _ -> Common.Unknown ""
                                                let _pattern = detail["pattern"].ToObject<string>()
                                                BitFlip ("bitflip", _var, _ty, _val, _pattern, None)
                                | _ -> Undef
        {Id = id; Trigger = trigger; Repeat = cycle; Target = target; Defect = defect }
    
    
    
module SafeLib =
    type Iteration =
    | Any of any:string
    | Cnt of iter:int * name:string
    | During of time:int
    
    type SafeItem = {
        Id: int
        Type: string
        Var: string
        Spec: Common.ValueType array //[v1,v2,v3]
        Limit: Iteration//-(any), in@<task name>(iteration nth in task name), t$n1(n1~n2 times)
    }

    type SafeModel = {
        id: int
        taskCode: string
    }
    
    let safeParser (jsonArray: JArray) =
        jsonArray.Children() |> Seq.map (fun (item: JToken) ->
                let id = item["id"].ToObject<int>()
                let ty = item["type"].ToObject<string>()
                let var = item["var"].ToObject<string>()
                let spec = (item["spec"].Values<JValue>() |> Seq.map (fun value ->
                    let ret = match value.Type with
                                | JTokenType.Integer -> Common.Integer (value.ToObject<int>())
                                | JTokenType.String -> Common.Str (value.ToObject<string>())
                                | JTokenType.Float -> Common.Float (value.ToObject<float>())
                                | _ -> Common.Unknown ""
                    ret
                    ))
                let limit = match item["limit"].ToObject<JObject>().First.Path with
                            | "any" -> Any ""
                            | "iter" -> let cnt = item["limit"].First.ToObject<int>()
                                        let name = item["limit"].First.Next.ToObject<string>()
                                        Cnt (cnt, name)
                            | "time" -> let time = item["limit"].First.ToObject<int>()
                                        During time
                            | _ -> Any ""
                {Id = id; Var = var; Spec = Seq.toArray spec; Type = ty; Limit = limit} 
            )
        |> Seq.toList
