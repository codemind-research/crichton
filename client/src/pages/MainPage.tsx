import Upload from "./components/UploadComp";
import Test from "./components/Test/TestComp";
import Log from "./components/LogComp";
import "./Page.scss";

export default function MainPage() {
  return (
    <div>
      <header className="main_header">
        <h1>Crichton</h1>
      </header>
      <div className="main_content">
        <Upload />
        <Test />
        <Log />
      </div>
    </div>
  );
}
