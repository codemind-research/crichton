import Style from "./Style";
import WhiteBoxResult from "./WhiteBoxResult";
import InjectionResult from "./InjectionResult";

export default function ResultReport(props: any) {
  const reportData = props.reportData;
  const testType = props.testType;

  const getCover = (): string => {
    const date = reportData.project.Date;
    return `
    <div class="page">
      <div class="title">
        <p class="text" id="title">Crichton</p>
        <p class="text" id="title">Test Tesult Report</p>
      </div>
      <div class="proData">
        <p class="text">Date : ${date}</p>
      </div>
    </div>`;
  };

  const getBodyCode = (): string => {
    const whiteBox = testType.whitebox.isSuccess ? getBodyData("whiteBox") : "";
    const injection = testType.injection.isSuccess ? getBodyData("injection") : "";

    const dividingLine = testType.whitebox && testType.injection ? `<p style="page-break-before: always"></p>` : "";
    return `${getCover()}
    <p style="page-break-before: always"></p>
    ${whiteBox}
    ${dividingLine}
    ${injection}
    `;
  };

  const getBodyData = (type: string): string => {
    switch (type) {
      case "whiteBox":
        return WhiteBoxResult(reportData);
      case "injection":
        return InjectionResult(reportData);
      default:
        return "";
    }
  };

  return `
  <!DOCTYPE html>
  <html>
    <head>
      <meta charset="utf-8" />
      <meta http-equiv="X-UA-Compatible" content="IE=edge" />
      <meta name="viewport" content="width=device-width, initial-scale=1" />
             
      <title>Crichton Report</title>
        ${Style()}
    </head>
    <body>
      ${getBodyCode()}
    </body>
   </html>
  `;
}
