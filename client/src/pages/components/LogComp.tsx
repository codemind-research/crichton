import { useState } from "react";
import "./Component.scss";

export default function LogComp() {
  const [logData, setLogData] = useState<Array<String>>([
    "[2023-08-24 16:46:00] Uploaded: demo.zip",
    "[2023-08-24 16:46:02] Start whitebox test...",
  ]);

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
}
