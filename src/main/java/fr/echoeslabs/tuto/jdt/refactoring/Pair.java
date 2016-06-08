package fr.echoeslabs.tuto.jdt.refactoring;

public class Pair<A, B> {
	private A first;
	private B second;

	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

	public int hashCode() {
		int hashFirst = first != null ? first.hashCode() : 0;
		int hashSecond = second != null ? second.hashCode() : 0;

		return (hashFirst + hashSecond) * hashSecond + hashFirst;
	}

	public String toString() {
		return "(" + first + ", " + second + ")";
	}

	public A first() {
		return first;
	}

	public B second() {
		return second;
	}

}
