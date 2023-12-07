export default function Style() {
  return `
    <style>
      * {
        color-adjust: exact;
        -webkit-print-color-adjust: exact;
        print-color-adjust: exact;
      }

      @media print, screen {
        * {
          color-adjust: exact;
          -webkit-print-color-adjust: exact;
          font-family: Arial, Helvetica, sans-serif;
        }
        .page {
          width: 210mm;
          height: 285mm;
          background: #fafafa;
          padding: 1cm;
          display: flex;
          flex-direction: column;
        }
        .title {
          flex: 1;
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
        }
        #title.text {
          text-align: center;
          font-weight: bold;
          font-size: 50px;
          margin-top: 10px;
        }
        .projectInfo {
          flex: 1;
          display: flex;
          justify-content: flex-end;
          align-items: flex-end;
          font-size: 20px;
          font-weight: bold;
          padding: 20px;
        }
        .plguinSubTitle {
          font-size: 30px;
          font-weight: bold;
          margin-bottom: 20px;
        }
        .info_table {
          font-size: 12px;
          border-collapse: collapse;
          margin-bottom: 50px;
        }
        .info_table td,
        .info_table th {
          border: 1px solid #ddd;
          padding: 8px;
        }
        .info_table th {
          padding-top: 12px;
          padding-bottom: 12px;
          text-align: left;
          background-color: #585858;
          color: white;
        }
        .info_table th.success {
          background: #317931;
        }
        .info_table th.failed {
          background: #793131;
        }

        .page.extend {
          height: fit-content;
        }
        .dirpath {
          opacity: 0.7;
        }
      }
      </style>
    `;
}
