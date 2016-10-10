package jcgp.gui.constants;

/**
 * Holds the constants used in the GUI.
 * 
 * @author Eduardo Pedroni
 *
 */
public abstract class Constants {

	/* Colours */
	/**
	 * A string containing the hexadecimal colour used for representing neutrality.
	 */
	public static final String NEUTRAL_COLOUR = "#FFFFFF";
	/**
	 * A string containing the hexadecimal colour used for representing a hard highlight.
	 * A "hard" select, for instance, happens when an output path is locked on the chromosome
	 * pane.
	 */
	public static final String HARD_HIGHLIGHT_COLOUR = "#5496FF";
	/**
	 * A string containing the hexadecimal colour used for a medium highlight.
	 * One example of such a selection is the colour applied to a node
	 * when it is hovered over.
	 */
	public static final String MEDIUM_HIGHLIGHT_COLOUR = "#75BAFF";
	/**
	 * A string containing the hexadecimal colour used for a soft highlight.
	 * When hovering over a node, its connections are soft-selected.
	 */
	public static final String SOFT_HIGHLIGHT_COLOUR = "#C7DFFF";
	/**
	 * A string containing the hexadecimal colour used for representing a good selection.
	 * Ideally a shade of green, used for instance when a manual connection is valid.
	 */
	public static final String GOOD_SELECTION_COLOUR = "#38C25B";
	/**
	 * A string containing the hexadecimal colour used for representing a neutral selection.
	 * Ideally a shade of yellow, used for instance when a manual connection is already the
	 * current connection.
	 */
	public static final String NEUTRAL_SELECTION_COLOUR = "#FFEF73";
	/**
	 * A string containing the hexadecimal colour used for representing a bad selection.
	 * Ideally a shade of red, use for instance when a manual connection is not valid.
	 */
	public static final String BAD_SELECTION_COLOUR = "#FF5C5C";
	
	
	
	/* Sizes and distances */
	/**
	 * The width or height of the area that can be clicked on
	 * to drag-resize a pane.
	 */
    public static final double RESIZE_MARGIN = 5.0;
    /**
	 * The minimum width of the settings pane, to prevent it
	 * from being resized beyond visibility.
	 */
    public static final double SETTINGS_MIN_WIDTH = 200;
    /**
	 * The minimum width of the console pane, to prevent it
	 * from being resized beyond visibility.
	 */
    public static final double CONSOLE_MIN_HEIGHT = 100;
    /**
     * Radius used for the representation of nodes in the grid.
     */
    public static final double NODE_RADIUS = 35;
	/**
	 * Spacing between each node.
	 */
	public static final double SPACING = 15;
	/**
	 * The angle across which the node's sockets are evently distributed.
	 */
	public static final double THETA = Math.PI / 1.4;
	/**
	 * The radius of the connection sockets, calculated as a function of
	 * NODE_RADIUS.
	 * 
	 */
	public static final double SOCKET_RADIUS = Math.sqrt(NODE_RADIUS) / 1.8;
	/**
	 * Size of the text in each node.
	 */
	public static final double NODE_TEXT = NODE_RADIUS / 2.5;
	
	
	/* CSS Styles
	 * TODO extract to stylesheet?
	 */
	/**
	 * The basic style of text boxes used in parameters.
	 */
	public static final String BASE_TEXT_STYLE = "-fx-border-color: #C9C9C9; -fx-border-radius: 2; -fx-padding: 0; ";
	/**
	 * The basic style of check boxes used in parameters.
	 */
	public static final String BASE_CHECKBOX_STYLE = "-fx-padding: 0; ";
	/**
	 * The style applied to invalid parameters, using BAD_SELECTION_COLOUR.
	 */
	public static final String INVALID_PARAMETER_STYLE = "-fx-background-color: " + BAD_SELECTION_COLOUR;
	/**
	 * The style applied to neutral parameters, using NEUTRAL_SELECTION_COLOUR.
	 */
	public static final String WARNING_PARAMETER_STYLE = "-fx-background-color: " + NEUTRAL_SELECTION_COLOUR;
	/**
	 * The style applied to valid parameters, using NEUTRAL_COLOUR.
	 */
	public static final String VALID_PARAMETER_STYLE = "-fx-background-color: " + NEUTRAL_COLOUR;	
    
}
