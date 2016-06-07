
public class Exercise1 {


	// CamelCaseMethodName refactoring will rename this method
	public void not_camel_case_method_similar_to_local_() {

	}
	public void not_camel_case_method_similar_to_local1() {

	}

	// CamelCaseMethodName refactoring will rename this method
	public void alreadyCamelCaseMethod() {

	}
	public void alreadyCamelCaseMethod1() {

	}

	public static void main(String[] args) {
		not_camel_case_method_similar_to_local_();
		not_camel_case_method_similar_to_local1();
		alreadyCamelCaseMethod1();

		// the method invocation refactoring should rename those 2 occurences
		this.not_camel_case_method_similar_to_local_();
		this.not_camel_case_method_similar_to_local1();
		this.alreadyCamelCaseMethod();

		ExternalObject eo = new ExternalObject();

		// the method invocation refactoring should not rename those 2 occurences
		eo.not_camel_case_method();
		eo.not_camel_case_method_similar_to_local();

	}


}
