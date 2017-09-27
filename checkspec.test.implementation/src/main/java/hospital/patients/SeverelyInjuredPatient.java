package hospital.patients;

import java.time.LocalTime;

@SuppressWarnings("unused")
public class SeverelyInjuredPatient extends AbstractPatient {

	int injuryRating;

	public SeverelyInjuredPatient(String patientName, LocalTime patientArrivalTime, int InjuryRating) {
		super(patientName, patientArrivalTime);
		injuryRating = InjuryRating;
		// implementation
	}

	@Override
	public int compareTo(AbstractPatient o) {
		return 0;
		// implementation
	}

	@Override
	public TreatmentPriority getPriority() {
		return null;
		// implementation
	}

	@Override
	public String getCatInfo() {
		return "";
		// implementation
	}

	private int getInjuryRating() {
		return injuryRating;
		// implementation
	}
}
