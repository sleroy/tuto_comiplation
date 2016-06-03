
public class Sample3 {


	// CamelCaseMethodName refactoring will rename this method
	public void not_camel_case_method_similar_to_local() {

	}

	// CamelCaseMethodName refactoring will rename this method
	public void alreadyCamelCaseMethod() {

	}

	public static void main(String[] args) {
		ExternalObject eo = new ExternalObject();

		// the method invocation refactoring should not rename those 2 occurences
		eo.not_camel_case_method();
		eo.not_camel_case_method_similar_to_local();

		// the method invocation refactoring should rename those 2 occurences
		this.not_camel_case_method_similar_to_local()
		this.alreadyCamelCaseMethod()
	}


}
