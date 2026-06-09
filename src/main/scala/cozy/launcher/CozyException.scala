package cozy.launcher

/*
 * @since   Jun.  9, 2026
 * @version Jun.  9, 2026
 * @author  ASAMI, Tomoharu
 */
final case class CozyException(message: String, code: Int = 1) extends RuntimeException(message)
