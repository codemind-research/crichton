import { PluginResult } from "../TypeDef";
import Style from "./Style";

export default function ResultReport(props: any) {
  const reportData: Array<PluginResult> = props.reportData;

  const dividingLine = `<p style="page-break-before: always"></p>`;
  const basicTableName = "info_table";
  const childTablename = "child_table";

  const getCoverPage = (): string => {
    const currentDate = new Date();
    const year = currentDate.getFullYear();
    const month = String(currentDate.getMonth() + 1).padStart(2, "0");
    const day = String(currentDate.getDate()).padStart(2, "0");

    const todayDate = `${year}-${month}-${day}`;
    return `
    <div class="page">
      <div class="title">
        <p class="text" id="title">Crichton</p>
        <p class="text" id="title">Test Tesult Report</p>
      </div>
      <div class="projectInfo">
        <p class="text">Date : ${todayDate}</p>
      </div>
    </div>`;
  };

  const getClassName = (infoItems: { [key: string]: any }): string => {
    const statusName: string | undefined = Object.keys(infoItems).find((itemName) => {
      const name = itemName.toLowerCase();
      return name.includes("success") || name.includes("status");
    });

    if (statusName === undefined) return "";
    else if (typeof infoItems[statusName] === "boolean") return infoItems[statusName] ? "success" : "failed";
    else return infoItems[statusName].toLowerCase().includes("success") ? "success" : "failed";
  };

  const getObjectInfoTableRow = (value: any, key: string): string => {
    return `
      <tr>
        <td width="30%">${key}</td>
        <td id="${key}">${value}</td>
      </tr>
    `;
  };

  const writeTableCode = (infoItems: { [key: string]: any }, tableClassName: string, tableTitle: string): string => {
    const childTableCodes: Array<string> = [];
    const tableRows: Array<string> = [];

    const className = getClassName(infoItems);
    const items = new Map(Object.entries(infoItems));

    items.forEach((value, key) => {
      if (Array.isArray(value)) childTableCodes.push(getArrayInfoTable(value, childTablename, key));
      else if (typeof value === "object") childTableCodes.push(writeTableCode(infoItems[key], childTablename, key));
      else tableRows.push(getObjectInfoTableRow(value, key));
    });

    return `
      <table class="${tableClassName}">
        <tr>
            <th colspan="2" class="${className}">${tableTitle}</th>
        </tr>
        ${tableRows.join("")}
      </table>
      ${childTableCodes.join("")}
    `;
  };

  const getArrayInfoTable = (
    infoItems: Array<{ [key: string]: any }>,
    tableClassName: string,
    tableTitle: string
  ): string => {
    return infoItems.map((items) => writeTableCode(items, tableClassName, tableTitle)).join("");
  };

  const getInfoTableData = (info: {
    [key: string]: {
      [key: string]: any;
    };
  }): string => {
    return Object.keys(info)
      .map((tableTitle) => {
        const infoItems = info[tableTitle];

        if (Array.isArray(infoItems)) return getArrayInfoTable(infoItems, basicTableName, tableTitle);
        else return writeTableCode(infoItems, basicTableName, tableTitle);
      })
      .join("");
  };

  const getPluginsResult = (): string => {
    return reportData
      .map((pluginResult) => {
        return `
        <div class="page extend">
          <p class="plguinSubTitle">${pluginResult.pluginName} Result</p>
          ${getInfoTableData(pluginResult.info)}
        </div>
      `;
      })
      .join(dividingLine);
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
    ${getCoverPage()}
    ${dividingLine}
    ${getPluginsResult()}
    </body>
   </html>
  `;
}
