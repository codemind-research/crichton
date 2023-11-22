import { useEffect, useState, useRef } from "react";
import { Status } from "../../util/Constants";
import "./Component.scss";

const LogComp = (props: any) => {
  const token = window.sessionStorage.getItem("accessToken");
  const logDivArea = useRef<HTMLDivElement>(null);
  const [logInterval, setLogInterval] = useState<NodeJS.Timer>();
  const [logData, setLogData] = useState<Array<string>>([]);

  useEffect(() => {
    const status = props.status;
    console.log("!!!!!", status);
    switch (status) {
      case Status.Created:
        setLogData([]);
        break;
      case Status.Testing:
        setLogInterval(
          setInterval(async (): Promise<void> => {
            await getLogFromServer();
            setScrollLocation();
          }, 5000)
        );
        break;
      case Status.Tested:
        if (logInterval !== undefined) {
          clearInterval(logInterval);
          setLogInterval(undefined);
          getLogFromServer();
          setScrollLocation();
        }
        break;
      default:
    }
  }, [props.status]);

  const getLogFromServer = async (): Promise<void> => {
    const response = await props.api.getTestProgress(token);
    const newLog: Array<string> = response.result.progress.split("\n");
    newLog.forEach((log: string) => {
      setLogData((prevLog) => [...prevLog, log]);
    });
  };

  const setScrollLocation = async (): Promise<void> => {
    if (logDivArea.current) {
      logDivArea.current.scrollTop = logDivArea.current.scrollHeight + 1000;
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
