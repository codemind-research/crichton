import Select from "./SelectComp";
import "./Test.scss";

export default function MainPage() {
  const uploadMarginStyle = {
    marginLeft: "80px",
  };
  const handleTestRunClick = () => {};
  const handleDownloadClick = () => {};

  return (
    <div className="running_test_component">
      <Select />
      <hr />
      <div className="test_option_button">
        <button onClick={handleTestRunClick}>Run</button>
        <button onClick={handleDownloadClick} style={uploadMarginStyle}>
          Download Report
        </button>
      </div>
    </div>
  );
}
