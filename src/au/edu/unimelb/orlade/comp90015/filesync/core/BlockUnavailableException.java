/**
 *
 */
package au.edu.unimelb.orlade.comp90015.filesync.core;

/**
 * @author aaron
 * @date 7th April 2013
 */

public class BlockUnavailableException extends Exception {
  private static final long serialVersionUID = 1L;

  /**
	 *
	 */
  public BlockUnavailableException() {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   */
  public BlockUnavailableException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param cause
   */
  public BlockUnavailableException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   * @param cause
   */
  public BlockUnavailableException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

}
