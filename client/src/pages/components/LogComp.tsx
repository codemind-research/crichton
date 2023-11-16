import { useEffect, useState } from "react";
import { Status } from "../../util/Constants";
import "./Component.scss";

const LogComp = (props: any) => {
  const API: { list: any; token: string; refresh: string } = props.api;
  const [logData, setLogData] = useState<Array<string>>([
    "[2023-08-24 16:46:00] Uploaded: demo.zip",
    "[2023-08-24 16:46:02] Start whitebox test...",
  ]);

  useEffect(() => {
    let interval: NodeJS.Timer | undefined = undefined;
    switch (props.status) {
      case Status.Created:
        setLogData([]);
        break;
      case Status.Testing:
        interval = setInterval(async () => {
          getLogToServer();
        }, 5000);
        break;
      case Status.Tested:
        if (interval) clearInterval(interval);
        break;
      default:
    }
  }, [props.status]);

  const getLogToServer = async () => {
    const data = {
      project: props.projectPath,
    };
    const response = await API.list.runTest(data, API.token);
    console.log(response); //로그가 올 거임
  };

  return (
    <div className="log_component">
      <h4>Log</h4>
      <div className="log_div_area">
        {logData?.map((log, index) => (
          <p key={index}>{log}</p>
        ))}
      </div>
    </div>
  );
};
export default LogComp;
