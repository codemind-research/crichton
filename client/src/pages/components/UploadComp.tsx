import { useRef, useState } from "react";
import { Status } from "../../util/Constants";
import "./Component.scss";

const UploadComp = (props: any) => {
  const token = window.sessionStorage.getItem("accessToken");
  const status = Number(window.sessionStorage.getItem("projectStatus"));
  const isTesting: boolean = status === Status.Testing;
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [fileList, setFileList] = useState<Array<File>>();

  const handleFileChange = (event: any) => {
    setFileList(event.target.files);
  };

  const handleSelectFileClick = (): void => {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };

  const handleUploadClick = async (): Promise<void> => {
    const zipFile = await makeZipFile();
    if (zipFile === undefined) return;
    const form = new FormData();
    form.append("file", zipFile, "crichton_project_temp.zip");

    const response = await props.api.uploadFile(form, token);
    props.setProjectPath(response.result.unzipPath);
  };

  const makeZipFile = async (): Promise<void> => {
    const zip = require("jszip")();
    if (fileList) {
      for (let file = 0; file < fileList.length; file++) {
        zip.file(fileList[file].name, fileList[file]);
      }

      const zipped = await zip.generateAsync({
        type: "blob",
      });
      return zipped;
    } else return undefined;
  };

  return (
    <div className="upload_input_component">
      <h3>Target source:</h3>
      <div className="input_with_button">
        <input type="file" directory="" webkitdirectory="" ref={fileInputRef} onChange={handleFileChange} />
        <span>
          {fileList?.length} {fileList ? "files selected" : ""}
        </span>
        <button type="submit" onClick={handleSelectFileClick} disabled={isTesting}>
          ...
        </button>
      </div>
      <button className="upload_button" onClick={handleUploadClick} disabled={isTesting}>
        Upload
      </button>
    </div>
  );
};

export default UploadComp;

declare module "react" {
  interface HTMLAttributes<T> extends AriaAttributes, DOMAttributes<T> {
    directory?: string;
    webkitdirectory?: string;
  }
}
