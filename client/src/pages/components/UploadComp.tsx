import { useRef, useState } from "react";
import { Status } from "../../util/Constants";
import "./Component.scss";

const UploadComp = (props: any) => {
  const API: { list: any; token: string; refresh: string } = props.api;
  const isTesting: boolean = props.status === Status.Testing;
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [fileList, setFileList] = useState<Array<File>>();

  const handleFileChange = (event: any) => {
    setFileList(event.target.files);
  };

  const handleSelectFileClick = () => {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };

  const handleUploadClick = async () => {
    const zipFile = await makeZipFile();
    if (zipFile === undefined) return;
    const form = new FormData();
    form.append("file", zipFile, "crichton_project_temp.zip");

    const response = await API.list.uploadFile(form, API.token);
  };

  const makeZipFile = async () => {
    const zip = require("jszip")();
    // const save = require("save-file");
    if (fileList) {
      for (let file = 0; file < fileList.length; file++) {
        zip.file(fileList[file].name, fileList[file]);
      }

      const zipped = await zip.generateAsync({
        type: "blob",
      });
      // await save(zipped, "crichton_project_temp.zip");
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
