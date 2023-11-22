import { WhiteBoxProject, WhiteBoxFile, WhiteBoxFunc } from "../Constants";

export default function WhiteBoxResult(data: any) {
  const project: WhiteBoxProject = data.project;
  const files: Array<WhiteBoxFile> = data.file;
  const funcs: Array<WhiteBoxFunc> = data.unit;

  const summaryTable = (): string => {
    const fileTotal = `${project.SuccessfulFiles} succ ${project.FailedFiles} fail`;
    const funcTotal = `${project.SuccessfulUnits} succ ${project.FailedUnits} fail`;
    const statCov = `${project.ExecutedLines} / ${project.Lines} (${project.LineCoverage}%)`;
    const branchCov = `${project.ExecutedBranches} / ${project.Branches} (${project.BranchCoverage}%)`;
    const pairCov = `${project.ExecutedPairs} / ${project.Pairs} (${project.PairCoverage}%)`;

    return `
    <table class="table">
        <tr>
            <th colspan="2">Summary</th>
        </tr>

        <tr>
            <td width="30%">Elapsed Time for PreBuild</td>
            <td id="prebuildETA">${project.PrebuildTime}</td>
        </tr>
        <tr>
            <td>Elapsed Time for Build</td>
            <td id="buildETA">${project.BuildTime}</td>
        </tr>
        <tr>
            <td>Elapsed Time for Test</td>
            <td id="testETA">${project.TestTime}</td>
        </tr>

        <tr>
            <td>Total Files</td>
            <td id="fileTotal">${fileTotal}</td>
        </tr>
        <tr>
            <td>Total Functions</td>
            <td id="funcTotal">${funcTotal}</td>
        </tr>
        <tr>
            <td>Total Testcases</td>
            <td id="testcaseTotal">${project.TestCases}</td>
        </tr>

        <tr>
            <td>Statement Coverage</td>
            <td id="stmtCoverage">${statCov}</td>
        </tr>
        <tr>
            <td>Branch Coverage</td>
            <td id="branchCoverage">${branchCov}</td>
        </tr>
        <tr>
            <td>MC/DC Coverage</td>
            <td id="pairCoverage">${pairCov}</td>
        </tr>
    </table>
    `;
  };

  const writeCovTableRow = (cellType: string, rowData: WhiteBoxFile | WhiteBoxFunc): string => {
    const writeCovRow = (executed: string, total: string, coverage: string) => {
      return `${executed} / ${total} (${coverage}%)`;
    };

    const name = "Files" in rowData ? rowData.Files : rowData.UnitNames;

    return `
    <tr>
        <${cellType}>${name}</${cellType}>
        <${cellType}>${writeCovRow(rowData.ExecutedLines, rowData.Lines, rowData.LineCoverage)}</${cellType}>
        <${cellType}>${writeCovRow(rowData.ExecutedBranches, rowData.Branches, rowData.BranchCoverage)}</${cellType}>
        <${cellType}>${writeCovRow(rowData.ExecutedPairs, rowData.Pairs, rowData.PairCoverage)}</${cellType}>
    </tr>
    `;
  };

  const writeFuncInfoTable = (fileName: string): string => {
    let functionTableData: string = "";
    funcs
      .filter((func) => func.FilePath === fileName)
      .forEach((func: WhiteBoxFunc) => {
        functionTableData += writeCovTableRow("td", func);
      });
    return functionTableData;
  };

  const testResultTable = (): string => {
    let resultTableData: string = "";
    files.forEach((file: WhiteBoxFile) => {
      if (file.Status == "TEST_SUCCESS") {
        resultTableData += `<table class="fileinfo">
            <tr>
                <th width="60%"></th>
                <th>Statement Coverage</th>
                <th>Branch Coverage</th>
                <th>MC/Dc Coverage</th>
            </tr>
            ${writeCovTableRow("th", file)}
            ${writeFuncInfoTable(file.Files)}
            </table>`;
      } else {
        resultTableData += `<table class="fileinfo">
            <tr>
                <th width="60%"> ${file.Files} </th>
                <th class="buildError"> ${file.Status} </th>
            </tr>
        </table>`;
      }
    });

    return resultTableData;
  };

  return `
  <div class="page extend">
    <p class="testType">WhiteBox Test Result</p>
    ${summaryTable()}
    ${testResultTable()}
  </div>
      `;
}
