# tuto_comiplation
Tutorial Refactoring &amp; Audit

The main goal of this lab is to apply high level transformations on concrete java code examples. The exercises consist in 3 steps :

1.  determine the expected output given the sample input files;
2.  implement the `refactor` method so that it generates the expected output;
3.	either make the method cover all possible cases, or identify (by commenting the method) the cases not covered;

The semantic for each refactoring is described shortly in the class comments.

## Exercise 1 -  MethodInvocationCamelCaseRefactoring

This refactoring renames locals methods declarations using camel case specification and impacts local method invocations.

Implement the `refactor` method from the class ̀`fr.echoeslabs.tuto.jdt.refactoring.MethodInvocationCamelCaseRefactoring`.

## Exercise 2 - SwitchOrderingRefactoring

This refactoring reorders the case blocks within switches in lexicographic order.

Implement the `refactor` method from the class ̀`fr.echoeslabs.tuto.jdt.refactoring.SwitchOrderingRefactoring`.

