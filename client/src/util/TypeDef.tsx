export enum Status {
  NotStarted,
  Created,
  Testing,
  Tested,
}

interface TestTypeInfo {
  selected: boolean;
  isTesting: boolean;
  result: boolean | undefined;
}

export interface TestTypes {
  [key: string]: TestTypeInfo;
}

export interface WhiteBoxProject {
  Date: string;
  PrebuildTime: string;
  BuildTime: string;
  TestTime: string;
  Files: string;
  SuccessfulFiles: string;
  FailedFiles: string;
  Units: string;
  SuccessfulUnits: string;
  FailedUnits: string;
  TestCases: string;
  ExecutedLines: string;
  Lines: string;
  LineCoverage: string;
  ExecutedBranches: string;
  Branches: string;
  BranchCoverage: string;
  ExecutedPairs: string;
  Pairs: string;
  PairCoverage: string;
}

export interface WhiteBoxFile {
  Files: string;
  Status: string;
  ExecutedLines: string;
  Lines: string;
  LineCoverage: string;
  ExecutedBranches: string;
  Branches: string;
  BranchCoverage: string;
  ExecutedPairs: string;
  Pairs: string;
  PairCoverage: string;
}

export interface WhiteBoxFunc {
  FilePath: string;
  UnitNames: string;
  ExecutedLines: string;
  Lines: string;
  LineCoverage: string;
  ExecutedBranches: string;
  Branches: string;
  BranchCoverage: string;
  ExecutedPairs: string;
  Pairs: string;
  PairCoverage: string;
}
