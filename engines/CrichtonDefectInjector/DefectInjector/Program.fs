open System.IO
open Newtonsoft.Json
open Newtonsoft.Json.Linq
open SpecLib

type TaskInfo = {
    name: string
    start: int
    cycle: int
    priority: int
}
let genFault (item: DefectLib.DefectItem) =
    let tName = "crichtonInject_" + item.Id.ToString()

    let genTaskCode =
        let mutable taskCode = "TASK(" + tName + ")\n{\n\t"

        let (op, _var, _val) =
            match item.Defect with
            | DefectLib.Taint(_, _var, _ty, _val, _pattern, _) ->
                match _pattern with
                | "random" -> (" += ", _var, _val)
                | "add" -> (" += ", _var, _val)
                | "sub" -> (" -= ", _var, _val)
                | "mul" -> (" *= ", _var, _val)
                | "div" -> (" /= ", _var, _val)
            | DefectLib.BitFlip(_, _var, _ty, _val, _pattern, _) -> (" ^= ", _var, _val)
            | _ -> ("", "", Common.Integer 0)

        let valStr =
            match _val with
            | Common.Integer x -> x.ToString()
            | Common.Str x -> x
            | Common.Float x -> x.ToString()
            | Common.Unknown x -> x

        taskCode <- taskCode + _var + op + valStr + ";\n\tTerminateTask();\n}\n"
        taskCode

    let genAlarmCode = "DeclareAlarm(" + tName + "_alarm);"

    let genTaskOil =
        "TASK "
        + tName
        + " {\n\tPRIORITY = 10;\n\tAUTOSTART = FALSE;\n\tACTIVATION = 1;\n\tSCHEDULE = FULL;\n};\n"

    let genAlarmOil (appMode: string) =
        "ALARM "
        + tName
        + "_alarm {\n\tCOUNTER = SystemCounter;\n\tACTION = ACTIVATETASK {\n\t\tTASK = "
        + tName
        + ";\n\t};\n\tAUTOSTART = TRUE { APPMODE = "
        + appMode
        + "; ALARMTIME = "
        + item.Trigger.ToString()
        + "; CYCLETIME = "
        + item.Repeat.ToString()
        + ";};\n};"

    let ret =
        { DefectLib.id = item.Id
          DefectLib.taskCode = genTaskCode
          DefectLib.alarmCode = genAlarmCode
          DefectLib.taskOil = genTaskOil
          DefectLib.alarmOil = genAlarmOil }

    ret

let faultRead (filename: string) =
    use sr = new StreamReader(filename)
    let jsonObj = sr.ReadToEnd() |> JsonConvert.DeserializeObject<JObject>
    let item = DefectLib.defectParser jsonObj
    let models = genFault item
    [ models ]

let genSafe (item: SafeLib.SafeItem) =
    let formatString =
        match item.Type with
        | "int" -> item.Var + "@->%d"
        | "float" -> item.Var + "@->%f"
        | "char" -> item.Var + "@->%c"
        | "string" -> item.Var + "@->%s"
        | _ -> item.Var + "@->%x"

    let var = item.Var

    (formatString, var)

let safeRead (filename: string) =
    use sr = new StreamReader(filename)
    let jsonArray = sr.ReadToEnd() |> JsonConvert.DeserializeObject<JArray>
    let json = SafeLib.safeParser jsonArray

    let pair = json |> List.map genSafe

    let (s1, s2) =
        pair
        |> List.fold (fun (acc1, acc2) (e1, e2) -> (acc1 + "@{" + e1, acc2 + ", " + e2)) ("[SafeMonitor] ", "")

    let preCode =
        "FUNC(void, OS_CODE) PostTaskHook()\n{\n\tTaskType t_id = 0;\n\tGetTaskID(&t_id);\n\tif(t_id != -1)\n\t\t"
#if POSIX
    let models =
        preCode + "printf(\"@crichton: " + s1 + "\\r\\n\", " + s2.Substring(1) + ");\n}"
#else
    let models =
        preCode
        + "trace_printf(\"@crichton: "
        + s1
        + "\\r\\n\", "
        + s2.Substring(1)
        + ");\n}"
#endif
    models

let modifiedFile
    (
        targetCode: string,
        oilCode: string,
        fModel: DefectLib.DefectModel list,
        safeCode: string,
        trampolinePath: string,
        workPath: string
    ) =
    //use srTarget = new StreamReader(targetName)
    let mutable targetContents = targetCode
    //use srOil = new StreamReader(oilName)
    let mutable oilContents = oilCode
    //let onlyName = Path.GetFileName(targetName)
    //oilContents <- oilContents.Replace(onlyName, onlyName + ".cr.c")
    let path_start = oilContents.IndexOf("TRAMPOLINE_BASE_PATH")
    let path_end = oilContents.IndexOf(";", path_start)

    oilContents <-
        oilContents.Replace(oilContents.[path_start..path_end], "TRAMPOLINE_BASE_PATH = \"" + trampolinePath + "\";")

    let sPos = oilContents.IndexOf("APPMODE") + 8
    let ePos = oilContents.IndexOf(" ", sPos)
    let appMode = oilContents.[sPos..ePos]

    targetContents <- targetContents + "\n//-----------------inject codes---------------------\n"
    let pos = oilContents.LastIndexOf("};")

    oilContents <-
        oilContents.Remove(pos, 2)
        + "\n//-----------------inject codes---------------------\n"

    for model in fModel do
        targetContents <- targetContents + model.alarmCode + "\n" + model.taskCode
        oilContents <- oilContents + model.taskOil + "\n" + model.alarmOil appMode

    oilContents <- oilContents + "\n};"
    targetContents <- targetContents + safeCode
    (*use swTarget = new StreamWriter(targetName + ".cr.c")
    use swOil = new StreamWriter(oilName + ".cr.oil")*)
    use swTarget = new StreamWriter(Path.Combine(workPath, "defectSim.c"))
    use swOil = new StreamWriter(Path.Combine(workPath, "defectSim.oil"))
    swTarget.Write(targetContents)
    swOil.Write(oilContents)

let mutable CFLAGS = "-ggdb -DPOSIX"
let mutable trampolinePath = "../../.."


let genOILCode (tasks: TestLib.TestDef) =
    let mutable userCode = ""
    let mutable cnt  = 1
    for task in tasks.Tasks do
        let alarmDef = (sprintf """
  ALARM crichton_alarm_%d {
    COUNTER = SystemCounter;
    ACTION = ACTIVATETASK { TASK = crichton_%d_%s; };
    AUTOSTART = TRUE { APPMODE = stdAppmode; ALARMTIME = %d; CYCLETIME = %d; };
  };
""" cnt cnt task.Name task.Start task.Cycle)
        let taskDef = (sprintf """
  TASK crichton_%d_%s {
    PRIORITY = %d;
    AUTOSTART = FALSE;
    ACTIVATION = 1;
    SCHEDULE = FULL;
  };
""" cnt task.Name task.Priority)
        userCode <- userCode + alarmDef + taskDef
        cnt <- cnt + 1
    let stopCode = (sprintf """
  ALARM crichton_alarm_stop {
    COUNTER = SystemCounter;
    ACTION = ACTIVATETASK { TASK = crichton_stop; };
    AUTOSTART = TRUE { APPMODE = stdAppmode; ALARMTIME = %d; CYCLETIME = 0; };
  };
  TASK crichton_stop {
    PRIORITY = 10;
    AUTOSTART = FALSE;
    ACTIVATION = 1;
    SCHEDULE = FULL;
  };
""" tasks.Stop)
    userCode <- userCode + stopCode
    let str = lazy (sprintf """
OIL_VERSION = "2.5";

IMPLEMENTATION trampoline {

    /* This fix the default STACKSIZE of tasks */
    TASK {
        UINT32 STACKSIZE = 32768 ;
    } ;

    /* This fix the default STACKSIZE of ISRs */
    ISR {
        UINT32 STACKSIZE = 32768 ;
    } ;
};

CPU only_one_periodic_task {
  OS config {
    STATUS = EXTENDED;
	TRACE = TRUE {
		FORMAT = json;
		PROC = TRUE;
        RESOURCE = TRUE;
        ALARM = TRUE;
        EVENT = TRUE;
    };
    BUILD = TRUE {
      APP_SRC = "defectSim.c";
      TRAMPOLINE_BASE_PATH = "%s";
      APP_NAME = "defectSim_exe";
      CFLAGS="%s";
      LINKER = "gcc";
      SYSTEM = PYTHON;
    };
    };
    APPMODE stdAppmode {};
    %s
    };
""" trampolinePath CFLAGS userCode)
    str

let genSrcCode (tasks: TestLib.TestDef) =
    let mutable code = ""
    let mutable taskCode = ""
    let mutable cnt  = 1
    let mutable addedList = []
    let addCode x = code <- code + x + "\n"
    for task in tasks.Tasks do
        if not (List.contains task.File addedList) then
            addCode task.Code
            addedList <- task.File :: addedList
        else
            ()
        
        let tempCode = (sprintf """
DeclareAlarm(crichton_alarm_%d);
TASK(crichton_%d_%s)
{
    %s();
    TerminateTask();
}
""" cnt cnt task.Name task.Name)
        taskCode <- taskCode + tempCode
        cnt <- cnt + 1
    addCode "\n//--------------crichton Code---------------\n"
    addCode """
#include "tpl_os.h"
int main(void)
{
    StartOS(OSDEFAULTAPPMODE);
    return 0;
}
"""
    addCode taskCode
    addCode """
DeclareAlarm(crichton_alarm_stop);
TASK(crichton_stop)
{
  printf("Shutdown\r\n");
  ShutdownOS(E_OK);
  TerminateTask();
}
"""
    code
    

let testRead (filename: string) =
    use sr = new StreamReader(filename)
    let jsonObj = sr.ReadToEnd() |> JsonConvert.DeserializeObject<JObject>
    let item = TestLib.testParser jsonObj
    item

let main =
    let args = System.Environment.GetCommandLineArgs()
    
    let (workPath, test, fault, safe, trampolinePath) =
        if args.Length < 6 then
            printfn "Bad Argument"
            printfn "Usage: DefectInjector <workspace> <test spec> <defect spec> <safe spec> <trampoline path>"
            exit -1
        else
            (args[1], args[2], args[3], args[4], args[5])

    let fModels = faultRead (fault)
    let safeCode = safeRead (safe)
    let testModel = testRead test
    let testCode = genSrcCode testModel
    let testOil = (genOILCode testModel).Value
    modifiedFile (testCode, testOil, fModels, safeCode, trampolinePath, workPath)
    
