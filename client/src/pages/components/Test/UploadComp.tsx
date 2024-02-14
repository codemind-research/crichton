import { useRef } from "react";
import "./Test.scss";

const UploadComp = (props: any) => {
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = (event: any) => {
    props.setFileList(event.target.files);
  };

  const handleSelectFileClick = (): void => {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };

  return (
    <div className="upload_input_component">
      <h3>Target source:</h3>
      <div className="input_with_button">
        <input type="file" directory="" webkitdirectory="" ref={fileInputRef} onChange={handleFileChange} />
        <span>{props.fileList.length > 0 ? props.fileList.length + " files selected" : ""}</span>
        <button type="submit" onClick={handleSelectFileClick} disabled={props.isTesting}>
          ...
        </button>
      </div>
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
