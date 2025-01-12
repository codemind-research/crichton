﻿module SpecLib

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
        ExtraSrcs: string list
        CFLAGS: string option
        LDFLAGS: string option
    }
    
    let testParser (obj: JObject):TestDef =
        let root = obj.Root
        let tasks = Seq.toList (root["tasks"].ToObject<JArray>().Children() |> Seq.map (fun (i: JToken) ->
            let item = i.ToObject<JObject>().Root
            let _name = item["name"].ToObject<string>()
            let _start = item["start"].ToObject<int>()
            let _cycle = item["cycle"].ToObject<int>()
            let _priority = item["priority"].ToObject<int>()
            let _file = item["file"].ToObject<string>()
            use codeTarget = new StreamReader(_file)
            let _code = codeTarget.ReadToEnd()
            {Name = _name; Start = _start; Cycle = _cycle; Priority = _priority; File = _file; Code = _code }
            
        ))

        let extras = root["extra_srcs"].ToObject<JArray>().Children()
                     |> Seq.map (fun (i: JToken) -> i.ToObject())
                     |> Seq.toList

        let stop = root["stop"].ToObject<int>()
        
        let cflags =
            if isNull root["cflags"] then
                None
            else
                Some <| root["cflags"].ToObject<string>()
        
        let ldflags =
            if isNull root["ldflags"] then
                None
            else
                Some <| root["ldflags"].ToObject<string>()
        
        { Tasks = tasks; Stop = stop; ExtraSrcs = extras; CFLAGS = cflags; LDFLAGS = ldflags }
        
module DefectLib =
    type DefectType =
        | Taint of kind:string * var:string * ty:string * value:Common.ValueType * pattern:string * code:string option
        | BitFlip of kind:string * var:string * ty:string * value:Common.ValueType * code:string option
        | Undef

    type DefectItem = {
        Id: int
        Target: string //target task name
        Trigger: int
        Repeat: int
        Defect: DefectType list
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
        let defect = item["defect"].ToObject<JArray>()
        
        let f acc (x:JToken) =
            match x["kind"].ToObject<string>() with
            | "taint" -> let _var = x["var"].ToObject<string>()
                         let _ty = x["type"].ToObject<string>()
                         let _val =  match x["value"].Type with
                                        | JTokenType.Integer -> Common.Integer (x["value"].ToObject<int>())
                                        | JTokenType.String -> Common.Str (x["value"].ToObject<string>())
                                        | JTokenType.Float -> Common.Float (x["value"].ToObject<float>())
                                        | _ -> Common.Unknown ""
                         let _pattern = x["pattern"].ToObject<string>()
                         Taint ("taint", _var, _ty, _val, _pattern, None)::acc
            | "bitflip" ->  let _var = x["var"].ToObject<string>()
                            let _ty = x["int_type"].ToObject<string>()
                            let _val =  match x["value"].Type with
                                            | JTokenType.Integer -> Common.Integer (x["value"].ToObject<int>())
                                            | JTokenType.String -> Common.Str (x["value"].ToObject<string>())
                                            | JTokenType.Float -> Common.Float (x["value"].ToObject<float>())
                                            | _ -> Common.Unknown ""
                            BitFlip ("bitflip", _var, _ty, _val, None)::acc
            | kind ->
                printf($"[WARN] Invalid defect kind: {kind}")
                acc
        
        printfn $"defect: {defect}"
        let defect' = defect |> Seq.fold f []
        {Id = id; Trigger = trigger; Repeat = cycle; Target = target; Defect = defect' }
    
    
    
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
