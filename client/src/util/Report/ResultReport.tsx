import { PluginResult } from "../TypeDef";
import Style from "./Style";

export default function ResultReport(props: any) {
  const reportData: Array<PluginResult> = props.data;

  const dividingLine = `<p style="page-break-before: always"></p>`;

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

  const getObjectArrayInfoTableRow = (infoItems: Array<{ [key: string]: any }>, nameArray: Array<string>): string => {
    return infoItems
      .map((items) => {
        const itemNames = Object.keys(items);
        const rowName = itemNames.find((itemName) => itemName.toLowerCase().includes("name"));
        const rowPath = itemNames.find((itemName) => itemName.toLowerCase().includes("path"));
        const rowTitle = rowName !== undefined ? rowName : rowPath !== undefined ? rowPath : "";

        const tableDatas = nameArray.map((name) => `<td id="${name}">${items[name]}</td>`).join("");
        return `
          <tr>
            <td>${items[rowTitle]}</td>
            ${tableDatas}
          </tr>
        `;
      })
      .join("");
  };

  const getObjectArrayInfoTable = (infoItems: { [key: string]: any }, className: string): string => {
    const innerObjName: Array<string> = [];
    const outerObj = Object.keys(infoItems);
    const outerObjName = outerObj.filter((itemName) => typeof infoItems[itemName] !== "object");

    const arrayObj: Array<{ [key: string]: any }> =
      infoItems[outerObj.filter((itemName) => Array.isArray(infoItems[itemName]))[0]];
    Object.keys(arrayObj[0]).forEach((name) => innerObjName.push(name));

    const outerTitle = outerObjName.find(
      (name) => name.toLowerCase().includes("path") || name.toLowerCase().includes("name")
    );
    const tableTitle =
      outerTitle !== undefined
        ? outerTitle
        : arrayObj
            .map((item) => Object.values(item))
            .reduce((preItem, currentItem) => preItem.filter((pre) => currentItem.includes(pre)))
            .filter((item) => isNaN(parseInt(item, 10)) && !isFinite(parseFloat(item)))[0];

    const commonNames: Array<string> = innerObjName.filter((inner) => outerObjName.includes(inner));

    const tableHeads = commonNames
      .map((name) => `<th class="${className}">${name.replace(/[_-]/g, " ").replace(/([A-Z])/g, " $1")}</th>`)
      .join("");
    const tableDatas = getObjectArrayInfoTableRow(arrayObj, commonNames);

    return `
      <table class="info_table">
        <tr>
            <th colspan="${commonNames.length + 1}" class="${className}">${tableTitle}</th>
        </tr>
        <tr>
        <th class="${className}"></th>
        ${tableHeads}
        </tr>
        ${tableDatas}
      </table>
    `;
  };

  const getObjectInfoTableRow = (infoItems: { [key: string]: any }): string => {
    return Object.keys(infoItems)
      .filter((itemName) => itemName !== "isSuccess" && itemName !== "Status")
      .map((itemName) => {
        return `
        <tr>
          <td width="30%">${itemName}</td>
          <td id="${itemName}">${infoItems[itemName]}</td>
        </tr>
      `;
      })
      .join("");
  };

  const writeTableCode = (infoItems: { [key: string]: any }, className: string, tableTitle: string): string => {
    const items = Object.keys(infoItems);
    if (items.filter((itemName) => typeof infoItems[itemName] === "object").length > 0)
      return getObjectArrayInfoTable(infoItems, className);

    return `
      <table class="info_table">
        <tr>
            <th colspan="2" class="${className}">${tableTitle}</th>
        </tr>
        ${getObjectInfoTableRow(infoItems)}
      </table>
    `;
  };

  const getArrayInfoTable = (infoItems: Array<{ [key: string]: any }>): string => {
    return infoItems
      .map((items) => {
        const status: string | undefined = items["Status"];
        const className = status === undefined ? "none" : status === "TEST_SUCCESS" ? "success" : "failed";
        const tableTitle = items["name"] === undefined ? "" : items["name"];
        return writeTableCode(items, className, tableTitle);
      })
      .join("");
  };

  const getInfoTableData = (info: {
    [key: string]: {
      [key: string]: any;
    };
  }): string => {
    return Object.keys(info)
      .map((tableTitle) => {
        const infoItems = info[tableTitle];
        if (Array.isArray(infoItems)) return getArrayInfoTable(infoItems);

        const isSuccess: boolean | undefined = infoItems["isSuccess"];
        const className = isSuccess === undefined ? "none" : isSuccess ? "success" : "failed";

        return writeTableCode(infoItems, className, tableTitle);
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
