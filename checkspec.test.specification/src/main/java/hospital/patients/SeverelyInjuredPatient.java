package hospital.patients;

import java.time.LocalTime;

@SuppressWarnings("unused")
public class SeverelyInjuredPatient extends AbstractPatient {

	private int injuryRating;

	public SeverelyInjuredPatient(String patientName,
			LocalTime patientArrivalTime, int patientInjuryRating) {
		super(patientName, patientArrivalTime);
		injuryRating = patientInjuryRating;
		// implementation
	}

	@Override
	public TreatmentPriority getPriority() {
		return null;
		// implementation
	}

	@Override
	public String getCatInfo() {
		return null;
		// implementation
	}

	@Override
	public int compareTo(AbstractPatient o) {
		return 0;
		// implementation
	}
}
