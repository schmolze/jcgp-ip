package jcgp.gui.dragresize;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import jcgp.gui.constants.Constants;

/**
 * This class adds vertical drag resize functionality to any 
 * arbitrary region provided. This is done by using the static
 * method {@code makeDragResizable()}.
 * <br><br>
 * This is based on a class by Andrew Till found on:
 * http://andrewtill.blogspot.co.uk/2012/12/dragging-to-resize-javafx-region.html
 * 
 */
public class VerticalDragResize {
	
	private boolean dragging = false;
	private final Region region;
	
	/**
	 * For internal use only, creates an instance of the actual
	 * resizer used.
	 * 
	 * @param region the region to make resizable.
	 */
	private VerticalDragResize(Region region) {
		this.region = region;
	}
	
	/**
	 * Makes the specified region drag resizable.
	 * This particular implementation only creates a resize 
	 * click-and-drag area on the top side of the region. 
	 * The resize area is defined by {@code GUI.RESIZE_MARGIN}.
	 * 
	 * @param region the region to make resizable.
	 */
	public static void makeDragResizable(final Region region) {
		// make the instance, this actually performs the resizing
		final VerticalDragResize dr = new VerticalDragResize(region);

		// set mouse listeners
		region.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
		        dr.mousePressed(event);
			}
		});
		region.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				dr.mouseDragged(event);
			}
		});
		region.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				dr.mouseMoved(event);
			}
		});
		region.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				dr.mouseReleased();
			}
		});
		
	}
	
	/**
	 * If the press happened in the resize area, raise the drag flag.
	 * 
	 * @param event the associated mouse event.
	 */
	private void mousePressed(MouseEvent event) {
		if(isInDraggableZone(event)) {
	        dragging = true;
        }
	}

	/**
	 * If drag flag is high, resize the region to match the mouse position.
	 * 
	 * @param event the associated mouse event.
	 */
	private void mouseDragged(MouseEvent event) {
		if(dragging) {
			double newHeight = region.getHeight() - event.getY();
			if (newHeight >= region.getMinHeight()) {
				region.setPrefHeight(newHeight);
			} else {
				region.setPrefHeight(region.getMinHeight());
			}
		}
	}
	
	/**
	 * Change the cursor if the mouse position overlaps with the resize area.
	 * 
	 * @param event the associated mouse event.
	 */
	private void mouseMoved(MouseEvent event) {
		if(isInDraggableZone(event) || dragging) {
			region.setCursor(Cursor.V_RESIZE);
        }
        else {
        	region.setCursor(Cursor.DEFAULT);
        }
	}

	/**
	 * Finish resizing.
	 */
	private void mouseReleased() {
		dragging = false;
		region.setCursor(Cursor.DEFAULT);
	}

	/**
	 * Assert whether the mouse cursor is in the draggable area defined by {@code GUI.RESIZE_MARGIN}.
	 * 
	 * @param event the associated mouse event.
	 * @return true if the mouse position is in the draggable area.
	 */
	private boolean isInDraggableZone(MouseEvent event) {
		return event.getY() < (Constants.RESIZE_MARGIN);
	}

}
