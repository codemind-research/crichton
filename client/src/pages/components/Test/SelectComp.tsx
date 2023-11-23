import { useRef, useState } from "react";
import { TestTypeInfo } from "../../../util/TypeDef";

const SelectComp = (props: any) => {
  const whitebox = props.testType.whitebox;
  const injection = props.testType.injection;

  const settingFileRef = useRef<HTMLInputElement>(null);
  const [settingFile, setSettingFile] = useState<string>("");
  const duration: number = props.duration;

  const handleCheckboxChange = (event: any) => {
    const { id, checked } = event.target;

    props.testTypeChange(id, checked);
  };
  const handleInputChange = (event: any) => {
    props.durationChange(event.target.value);
  };

  const handleFileChange = (event: any) => {
    const selectedFile = event.target.files[0];
    if (selectedFile) setSettingFile(selectedFile.name);
    props.settingFileSelect(selectedFile);
  };
  const handleSelectFileClick = (): void => {
    if (settingFileRef.current) {
      settingFileRef.current.click();
    }
  };

  const getTypeLabelColor = (labelType: TestTypeInfo): string => {
    if (labelType.isTesting === undefined && labelType.isSuccess === undefined) return "black";
    else if (labelType.isTesting) return "gray";
    else if (labelType.isSuccess) return "blue";
    else return "red";
  };

  const whiteBoxColorStyle: Object = {
    color: getTypeLabelColor(whitebox),
  };

  const injectionColorStyle: Object = {
    color: getTypeLabelColor(injection),
  };

  return (
    <div className="select_test_type_component">
      <div className="checkbox_option">
        <input type="checkbox" id="whitebox" value={whitebox.selected} onChange={handleCheckboxChange} />
        <label htmlFor="whitebox" style={whiteBoxColorStyle}>
          Whitebox Unit Test
        </label>
        <div className="option">
          <div className="input_with_button">
            <label htmlFor="quantity">· Project Setting: </label>
            <input type="file" ref={settingFileRef} onChange={handleFileChange} />
            <span>{settingFile}</span>
            <button type="submit" onClick={handleSelectFileClick} disabled={false}>
              ...
            </button>
          </div>
        </div>
      </div>
      <div className="checkbox_option">
        <input type="checkbox" id="injection" value={injection.selected} onChange={handleCheckboxChange} />
        <label htmlFor="injection" style={injectionColorStyle}>
          Injection Test
        </label>
        <div className="option">
          <label htmlFor="quantity">· Test duration: </label>
          <input type="number" id="quantity" min="1" value={duration} onChange={handleInputChange} />
          <span>sec</span>
        </div>
      </div>
    </div>
  );
};
export default SelectComp;
