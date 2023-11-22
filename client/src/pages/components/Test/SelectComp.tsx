const SelectComp = (props: any) => {
  const checkedTestType: { whitebox: string; injection: string } = {
    whitebox: props.testType.whitebox,
    injection: props.testType.injection,
  };
  const duration: number = props.duration;

  const handleCheckboxChange = (event: any) => {
    const { id, checked } = event.target;

    props.testTypeChange(id, checked);
  };
  const handleInputChange = (event: any) => {
    props.durationChange(event.target.value);
  };

  return (
    <div className="select_test_type_component">
      <input type="checkbox" id="whitebox" value={checkedTestType.whitebox} onChange={handleCheckboxChange} />
      <label htmlFor="whitebox">Whitebox Unit Test</label>
      <div className="injection_checkbox_option">
        <input type="checkbox" id="injection" value={checkedTestType.injection} onChange={handleCheckboxChange} />
        <label htmlFor="injection">Injection Test</label>
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
