
public class Exercise2 {

	public void switch1(final int key) {
		switch (key) {
		case 4:
			System.out.println("quatre");
			break;
		case 2:
			System.out.println("||");
			break;
		default:
			System.out.println(key);
		}
	}

	public void switch2(final String key) {
		switch (key.toLowerCase()) {
		case "true":
		case "false":
			System.out.println("boolean");
			break;
		default:
			throw new RuntimeException("Unsupported boolean litteral " + key);
		}
	}

	enum Enum1 {
		VAL1, VAL2, VAL3
	};

	public void switch3(Enum1 key) {
		switch (key) {
		case VAL3:
			System.out.println("MAX ACHIEVED");
			for(;;) {
				if (true)
					break;
			}
			break;
		case VAL1:
		case VAL2:
			System.out.println("Not Maximul");
		}
	}

	public void switch4(int key) {

		switch (key) {
		case 4:
		case -4:
			System.out.println("E");
		case 3:
		case -3:
			System.out.println("D");
		case 2:
		case -2:
			System.out.println("C");
		case 1:
		case -1:
			System.out.println("B");
		case 0:
			System.out.println("A");
		default:
			System.out.println(".");
		}
	}

	public void switch9000(int key) {
		loop: for (int i = 0; i < 9000; i++) {
			switch (key) {
			case 0:
				break loop;
			case 9000:
				i--;
			default:
				compute(i);
			}
		}
	}

	private void compute(int i) {
		// do some stuff
	}

}
