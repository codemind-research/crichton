export default function InjectionResult(data: any) {
  const resultTableScript = (): string => {
    return ``;
  };
  const resultTable = (): string => {
    return `<table class="table" id="projInfo">
    ${resultTableScript()}
      </table>`;
  };

  return `
    <div class="page">
        <p class="testType">Injection Test Result</p>
        ${resultTable()}
    </div>
    `;
}
