import { useEffect, useState, useRef } from "react";
import { Status } from "../../util/TypeDef";
import "./Component.scss";

const LogComp = (props: any) => {
  const token = window.localStorage.getItem("accessToken");
  const logDivArea = useRef<HTMLDivElement>(null);
  const [logInterval, setLogInterval] = useState<NodeJS.Timer>();
  const [logData, setLogData] = useState<Array<string>>([]);

  useEffect(() => {
    const status = props.status;
    let logPos = 0;
    switch (status) {
      case Status.Testing.valueOf():
        setLogData([]);
        setLogInterval(
          setInterval(async (): Promise<void> => {
            logPos = await getLogFromServer(logPos);
          }, 5000)
        );
        break;
      case Status.Tested.valueOf():
        if (logInterval !== undefined) {
          clearInterval(logInterval);
          setLogInterval(undefined);
          getLogFromServer(logPos);
        }
        break;
      default:
    }
  }, [props.status]);

  useEffect(() => {
    setScrollLocation();
  }, [logData]);

  const getLogFromServer = async (logPos: number): Promise<number> => {
    const data = { maxline: 100000, startpos: logPos };
    const response = await props.api.getTestProgress(data, token);
    if (!response.successful) return logPos;
    const result = response.result;
    const newLog: Array<string> = result.text.split("\n");
    newLog.forEach((log: string) => {
      setLogData((prevLog) => [...prevLog, log]);
    });
    return result.endpos;
  };

  const setScrollLocation = async (): Promise<void> => {
    if (logDivArea.current) {
      logDivArea.current.scrollTop = logDivArea.current.scrollHeight;
    }
  };

  return (
    <div className="log_component">
      <h4>Log</h4>
      <div className="log_div_area" ref={logDivArea}>
        {logData?.map((log, index) => (
          <p key={index}>{log}</p>
        ))}
      </div>
    </div>
  );
};
export default LogComp;
