package fr.openwide.core.imports.excel.event;

import fr.openwide.core.imports.excel.event.ExcelImportEvent.ExcelImportErrorEvent;
import fr.openwide.core.imports.excel.event.ExcelImportEvent.ExcelImportInfoEvent;
import fr.openwide.core.imports.excel.event.exception.ExcelImportContentException;
import fr.openwide.core.imports.excel.event.exception.ExcelImportHeaderLabelMappingException;
import fr.openwide.core.imports.excel.event.exception.ExcelImportMappingException;
import fr.openwide.core.imports.excel.location.ExcelImportLocation;

/**
 * An interface for classes handling excel import events (mainly errors for the moment).
 * <p>This class assumes that excel import include error-tolerant phases (for instance, when doing only data loading), and phases where no error can be tolerated
 * (for instance, when using the loaded data are used to update database entities). That's the point of the {@link #checkNoErrorOccurred()} method: implementors may choose
 * to silently stack errors when {@link #error(String, ExcelImportLocation)} or {@link #missingValue(String, ExcelImportLocation)} are called, and delay exception throwing
 * until {@link #checkNoErrorOccurred()} is called.
 */
public interface IExcelImportEventHandler {
	
	void headerLabelMappingError(String expectedHeaderLabel, int indexAmongMatchedColumns, ExcelImportLocation location) throws ExcelImportHeaderLabelMappingException;
	
	void checkNoMappingErrorOccurred() throws ExcelImportMappingException;
	
	void event(ExcelImportErrorEvent event, ExcelImportLocation location, String message, Object ... args) throws ExcelImportContentException;
	
	void event(ExcelImportInfoEvent event, ExcelImportLocation location, String message, Object ... args);
	
	/**
	 * Checks that no error occurred since the last call to {@link #checkNoErrorOccurred()} or {@link #resetErrors()}, throwing an exception if an error has actually occurred.
	 */
	void checkNoErrorOccurred() throws ExcelImportContentException;
	
	void resetErrors();
	
}
