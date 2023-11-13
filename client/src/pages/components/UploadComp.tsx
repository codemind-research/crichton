import { useRef, useState, InputHTMLAttributes } from "react";
import "./Component.scss";

export default function UploadComp() {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [selectFile, setSelectFile] = useState<File>();

  const handleFileChange = (event: any) => {
    setSelectFile(event.target.files[0]);
  };

  const handleSelectFileClick = () => {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };

  const handleUploadClick = () => {};

  return (
    <div className="upload_input_component">
      <h3>Target source:</h3>
      <div className="input_with_button">
        <input type="file" ref={fileInputRef} onChange={handleFileChange} />
        <span>{selectFile?.name}</span>
        <button type="submit" onClick={handleSelectFileClick}>
          ...
        </button>
      </div>
      <button className="upload_button" onClick={handleUploadClick}>
        Upload
      </button>
    </div>
  );
}
