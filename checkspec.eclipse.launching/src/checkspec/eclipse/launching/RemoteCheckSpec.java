package checkspec.eclipse.launching;

import checkspec.cli.CommandLineException;
import checkspec.cli.CommandLineInterface;

public class RemoteCheckSpec extends CommandLineInterface {

	public static void main(String[] args) {
		try {
			new RemoteCheckSpec().parse(args);
		} catch (CommandLineException e) {
			e.printStackTrace();
		}
	}

//	@Override
//	protected Outputter parseOutputter(CommandLine commandLine) throws CommandLineException {
//		return report -> {
//			System.out.println(report);
//		};
//	}
}
