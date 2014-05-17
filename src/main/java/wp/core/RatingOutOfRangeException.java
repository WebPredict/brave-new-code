package wp.core;

public class RatingOutOfRangeException extends Exception {

	public RatingOutOfRangeException (int rating) {
		super ("Rating " + rating + " is out of range.");
	}
}
