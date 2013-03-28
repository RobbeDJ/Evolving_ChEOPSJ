package be.ac.ua.ansymo.cheopsj.model.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;


public class ExportGrooveGraphHandler extends AbstractHandler {

		/**
		 * Load the state of the model
		 */
		public Object execute(ExecutionEvent event) throws ExecutionException {

			ModelManager.getInstance().printGraphForGroove();
			// should now also refresh the change view?
			new RefreshHandler().execute(event);
			return null;
		}
	}
