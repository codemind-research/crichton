import { useState, useEffect } from "react";
import Upload from "./components/UploadComp";
import Test from "./components/Test/TestComp";
import Log from "./components/LogComp";
import { Status } from "../util/Constants";
import "./Page.scss";

export default function MainPage() {
  const apiList = require("../api/APIList").default;
  const [accessToken, setAccessToken] = useState<string>("");
  const [refreshToken, setRefreshToken] = useState<string>("");
  const [projectPath, setProjectPath] = useState<String>();
  const [projectStatus, setProjectStatus] = useState<Status>(Status.NotStarted);

  // accessToken이 기본 token으로 사용되고,
  // accessToken이 만료되는 경우에 refreshToken을 사용하여 갱신된 accessToken을 받아와야함.
  const API: {
    list: any;
    token: string;
    refresh: string;
  } = { list: apiList, token: accessToken, refresh: refreshToken };

  useEffect(() => {
    getToken();
  }, []);

  const getToken = async () => {
    const tokenData = await API.list.getToken();
    if (tokenData.successful) {
      setAccessToken(tokenData.result.accessToken);
      setRefreshToken(tokenData.result.refreshToken);
    } else {
      alert("Server is not running");
    }
  };

  const handleProjectPathChange = (path: string) => {
    if (path) {
      setProjectPath(path);
      setProjectStatus(Status.Created);
    }
  };
  const handleStatusChange = (status: Status) => {
    if (status) setProjectStatus(status);
  };

  return (
    <div>
      <header className="main_header">
        <h1>Crichton</h1>
      </header>
      <div className="main_content">
        <Upload api={API} status={projectStatus} setProjectPath={handleProjectPathChange} />
        <Test api={API} status={projectStatus} projectPath={projectPath} setStatus={handleStatusChange} />
        <Log api={API} status={projectStatus} projectPath={projectPath} />
      </div>
    </div>
  );
}
