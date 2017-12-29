package org.iglooproject.wicket.more.fileapi.behavior;

import java.io.IOException;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.wicketstuff.wiquery.core.javascript.JsStatement;
import org.wicketstuff.wiquery.core.javascript.JsUtils;
import org.wicketstuff.wiquery.core.options.LiteralOption;
import org.wicketstuff.wiquery.core.options.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.iglooproject.wicket.more.fileapi.model.FileApiFile;
import org.iglooproject.wicket.more.fileapi.model.FileUploadFailType;
import org.iglooproject.wicket.more.fileapi.model.FileUploadMode;
import org.iglooproject.wicket.more.markup.html.template.js.jquery.plugins.fileuploadglue.FileUploadGlueJavaScriptResourceReference;
import org.iglooproject.wicket.more.markup.html.template.js.jquery.plugins.util.JsScopeFunction;

public abstract class FileUploadBehavior extends AbstractDefaultAjaxBehavior {

	private static final long serialVersionUID = -5670865313571005330L;

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadBehavior.class);

	/**
	 * nom du paramètre utilisé pour transmettre les fichiers
	 */
	public static final String PARAMETERS_FILE_UPLOAD = "files";

	/**
	 * nom du paramètre utilisé pour transmettre les informations issues de file API (métadonnées)
	 */
	private static final String PARAMETERS_FILE_LIST = "fileList";

	/**
	 * nom du paramètre utilisé pour transmettre les informations à la fin de l'upload (fichiers uploadés avec succès)
	 */
	private static final String PARAMETERS_UPLOAD_DONE_SUCCESS_FILE_LIST = "successFileList";

	/**
	 * nom du paramètre utilisé pour transmettre les informations à la fin de l'upload (fichiers non uploadés)
	 */
	private static final String PARAMETERS_UPLOAD_DONE_ERROR_FILE_LIST = "errorFileList";

	/**
	 * nom du paramètre utilisé pour transmettre les informations à la fin de l'upload (fichiers non uploadés)
	 */
	private static final String PARAMETERS_UPLOAD_FAILS_ERROR_MESSAGE = "uploadFailsErrorMessage";

	/**
	 * nom de la variable globale d'échange ; permet de communiquer dans informations javascript
	 * via le AjaxRequestTarget.
	 */
	private static final String PARAMETERS_DATA_VARIABLE_NAME = "dataVariableName";
	/**
	 * paramètre qui stocke le mode (permet de dispatcher les appels ajax)
	 */
	private static final String PARAMETERS_MODE = "mode";
	/**
	 * paramètre qui stocke le le type d'échec
	 */
	private static final String PARAMETERS_FAIL_TYPE = "failType";
	/**
	 * nom de l'option qui stocke le nom de paramètre POST utilisé pour transmettre les fichiers
	 */
	private static final String OPTIONS_PARAM_NAME = "paramName";
	/**
	 * nom de l'option qui indique la callback appelée sur le change
	 */
	private static final String OPTIONS_CHANGE = "change";
	/**
	 * nom de l'option qui indique la méthode wicket qui communique avec le serveur
	 * pattern -> change -> fileuploadglue -> onChangeCallback
	 */
	private static final String OPTIONS_ON_CHANGE_CALLBACK = "onChangeCallback";
	/**
	 * nom de l'option qui indique la méthode wicket qui communique avec le serveur
	 * pattern -> change -> fileuploadglue -> onUploadDoneCallback
	 */
	private static final String OPTIONS_ON_UPLOAD_DONE_CALLBACK = "onUploadDoneCallback";
	/**
	 * nom de l'option qui indique la méthode wicket qui communique avec le serveur
	 * pattern -> change -> fileuploadglue -> onUploadFailsCallback
	 */
	private static final String OPTIONS_ON_UPLOAD_FAILS_CALLBACK = "onUploadFailsCallback";
	/**
	 * nom de l'option pour l'auto-upload (désactivé dans notre cas)
	 */
	private static final String OPTIONS_AUTO_UPLOAD = "autoUpload";
	/**
	 * nom de l'option pour la callback de progrès de l'upload
	 */
	private static final String OPTIONS_PROGRESSALL = "progressall";
	/**
	 * nom de l'option de l'url pour l'upload des fichiers
	 */
	private static final String OPTIONS_URL = "url";

	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final Component progressComponent;

	public FileUploadBehavior(Component progressComponent) {
		super();
		this.progressComponent = progressComponent;
	}

	protected abstract String getFileUploadUrl();

	/**
	 * File change callback. Override to perform custom tasks on file change.
	 */
	protected abstract List<FileApiFile> onFileChange(AjaxRequestTarget target, List<FileApiFile> fileList);

	/**
	 * Override to perform custom tasks on error.
	 * 
	 * @param mode callback mode being performed when error occurs.
	 * @param target
	 * @param exception
	 */
	protected void onError(FileUploadMode mode, AjaxRequestTarget target, Exception exception) {
	}

	/**
	 * Override to perform custom logging on Unknown mode error.
	 * 
	 * @param exception
	 */
	protected void logUnknownMode(Exception exception) {
		IRequestParameters req = RequestCycle.get().getRequest().getRequestParameters();
		if (!req.getParameterValue(PARAMETERS_MODE).isEmpty()) {
			LOGGER.error("Error reading mode ; {}", req.getParameterValue(PARAMETERS_MODE).toString(), exception); 
		} else {
			LOGGER.error("Error reading mode ; empty parameter.", exception);
		}
	}

	/**
	 * Override to perform custom logging on file list decoding error.
	 * 
	 * @param exception
	 */
	protected void logReadFileListParameterError(Exception exception) {
		IRequestParameters req = RequestCycle.get().getRequest().getRequestParameters();
		if (!req.getParameterValue(PARAMETERS_FILE_LIST).isEmpty()) {
			LOGGER.error("Error reading {} parameter ; {}", PARAMETERS_FILE_LIST, req.getParameterValue(PARAMETERS_FILE_LIST).toString(), exception); 
		} else {
			LOGGER.error("Error reading {} parameter ; empty parameter.", PARAMETERS_FILE_LIST, exception);
		}
	}

	/**
	 * wicket callback - performs method dispatching based on {@link FileUploadBehavior#PARAMETERS_MODE} parameter.
	 * 
	 * @param target
	 */
	@Override
	protected void respond(AjaxRequestTarget target) {
		IRequestParameters req = RequestCycle.get().getRequest().getRequestParameters();
		FileUploadMode mode;
		try {
			mode = req.getParameterValue(PARAMETERS_MODE).toEnum(FileUploadMode.class);
		} catch (Exception e) {
			// null or unknown value
			logUnknownMode(e);
			return;
		}
		
		switch (mode) {
		case CHANGE:
			respondChange(target);
			break;
		case UPLOAD_DONE:
			respondUploadDone(target);
			break;
		case UPLOAD_FAILS:
			respondUploadFails(target);
			break;
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * change wicket callback.
	 * 
	 * @param target
	 */
	protected void respondUploadDone(AjaxRequestTarget target) {
		IRequestParameters req = RequestCycle.get().getRequest().getRequestParameters();
		try {
			List<FileApiFile> successfileList = readSuccessFileApiFiles(req);
			List<FileApiFile> errorfileList = readErrorFileApiFiles(req);
			onFileUploadDone(target, successfileList, errorfileList);
		} catch (Exception e) {
			logReadFileListParameterError(e);
			onError(FileUploadMode.UPLOAD_DONE, target, e);
			return;
		}
	}

	/**
	 * change wicket callback.
	 * 
	 * @param target
	 */
	protected void respondUploadFails(AjaxRequestTarget target) {
		IRequestParameters req = RequestCycle.get().getRequest().getRequestParameters();
		try {
			String errorMessage = req.getParameterValue(PARAMETERS_UPLOAD_FAILS_ERROR_MESSAGE).toString("");
			FileUploadFailType failType = FileUploadFailType.fromName(req.getParameterValue(PARAMETERS_FAIL_TYPE).toString(""));
			onFileUploadFails(target, failType, errorMessage);
		} catch (Exception e) {
			logReadFileListParameterError(e);
			onError(FileUploadMode.UPLOAD_FAILS, target, e);
			return;
		}
	}

	protected abstract void onFileUploadDone(AjaxRequestTarget target, List<FileApiFile> successFileList, List<FileApiFile> errorFileList);

	protected abstract void onFileUploadFails(AjaxRequestTarget target, FileUploadFailType failType, String errorMessage);

	/**
	 * change wicket callback.
	 * 
	 * @param target
	 * @return to upload file list (upload will be processed by objectUrl comparison)
	 */
	protected void respondChange(AjaxRequestTarget target) {
		IRequestParameters req = RequestCycle.get().getRequest().getRequestParameters();
		try {
			List<FileApiFile> fileList = readFileApiFiles(req);
			List<FileApiFile> acceptedFiles = onFileChange(target, fileList);
			target.prependJavaScript("window." + req.getParameterValue(PARAMETERS_DATA_VARIABLE_NAME).toString() + " = JSON.parse(" + JsUtils.doubleQuotes(writeFileApiFiles(acceptedFiles), true) + "); if (console) { console.log(window." + req.getParameterValue(PARAMETERS_DATA_VARIABLE_NAME).toString() + "); }");
		} catch (Exception e) {
			logReadFileListParameterError(e);
			onError(FileUploadMode.CHANGE, target, e);
			return;
		}
	}

	/**
	 * Appel des callbacks onSuccess / onFailure
	 */
	@Override
	protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
		super.updateAjaxAttributes(attributes);
		AjaxCallListener ajaxCallListener = new AjaxCallListener();
		ajaxCallListener.onFailure("onFailure && onFailure(attrs, jqXHR, errorMessage, textStatus);");
		ajaxCallListener.onSuccess("onSuccess && onSuccess(attrs, jqXHR, data, textStatus);");
		attributes.getAjaxCallListeners().add(ajaxCallListener);
	}

	/**
	 * wicket javascript callback glue - function (fileList, onSuccess, onFailure) ; mode = 'change'
	 */
	private CharSequence getOnChangeCallbackScript() {
		CallbackParameter callbackParameterMode = CallbackParameter.resolved(PARAMETERS_MODE, JsUtils.quotes(FileUploadMode.CHANGE.name()));
		// installation dans le scope des callbacks d'échec et de succès
		// réutilisés par les IAjaxCallListener
		CallbackParameter callbackParameterFileList = CallbackParameter.explicit(PARAMETERS_FILE_LIST);
		CallbackParameter callbackParameterDataVariableName = CallbackParameter.explicit(PARAMETERS_DATA_VARIABLE_NAME);
		CallbackParameter callbackParameterOnSuccess = CallbackParameter.context("onSuccess");
		CallbackParameter callbackParameterOnFailure = CallbackParameter.context("onFailure");
		return getCallbackFunction(callbackParameterMode, callbackParameterDataVariableName, callbackParameterFileList, callbackParameterOnSuccess, callbackParameterOnFailure);
	}

	/**
	 * wicket javascript callback glue - function (fileList, onSuccess, onFailure) ; mode = 'change'
	 */
	private CharSequence getOnUploadFailsCallbackScript() {
		CallbackParameter callbackParameterMode = CallbackParameter.resolved(PARAMETERS_MODE, JsUtils.quotes(FileUploadMode.UPLOAD_FAILS.name()));
		// installation dans le scope des callbacks d'échec et de succès
		// réutilisés par les IAjaxCallListener
		CallbackParameter callbackParameterUploadFailType = CallbackParameter.explicit(PARAMETERS_FAIL_TYPE);
		CallbackParameter callbackParameterErrorMessage = CallbackParameter.explicit(PARAMETERS_UPLOAD_FAILS_ERROR_MESSAGE);
		CallbackParameter callbackParameterDataVariableName = CallbackParameter.explicit(PARAMETERS_DATA_VARIABLE_NAME);
		CallbackParameter callbackParameterOnSuccess = CallbackParameter.context("onSuccess");
		CallbackParameter callbackParameterOnFailure = CallbackParameter.context("onFailure");
		return getCallbackFunction(callbackParameterMode, // mode
				callbackParameterDataVariableName, // variable d'échange
				callbackParameterUploadFailType, // type d'échec
				callbackParameterErrorMessage, // message d'erreur
				callbackParameterOnSuccess, callbackParameterOnFailure);
	}

	/**
	 * wicket javascript callback glue - function (fileList, onSuccess, onFailure) ; mode = 'change'
	 */
	private CharSequence getOnUploadDoneCallbackScript() {
		CallbackParameter callbackParameterMode = CallbackParameter.resolved(PARAMETERS_MODE, JsUtils.quotes(FileUploadMode.UPLOAD_DONE.name()));
		// installation dans le scope des callbacks d'échec et de succès
		// réutilisés par les IAjaxCallListener
		CallbackParameter callbackParameterSuccessFileListName = CallbackParameter.explicit(PARAMETERS_UPLOAD_DONE_SUCCESS_FILE_LIST);
		CallbackParameter callbackParameterErrorFileListName = CallbackParameter.explicit(PARAMETERS_UPLOAD_DONE_ERROR_FILE_LIST);
		CallbackParameter callbackParameterDataVariableName = CallbackParameter.explicit(PARAMETERS_DATA_VARIABLE_NAME);
		CallbackParameter callbackParameterOnSuccess = CallbackParameter.context("onSuccess");
		CallbackParameter callbackParameterOnFailure = CallbackParameter.context("onFailure");
		return getCallbackFunction(callbackParameterMode, // mode
				callbackParameterDataVariableName, // variable d'échange
				callbackParameterSuccessFileListName, callbackParameterErrorFileListName, // listes des succès et échecs
				callbackParameterOnSuccess, callbackParameterOnFailure);
	}

	/**
	 * widget initialization code generation.
	 * 
	 * @param component
	 */
	protected JsStatement statement(Component component) {
		Options options = new Options();
		// javascript callback method called on change event
		options.put(OPTIONS_CHANGE, new JsScopeFunction("$.fileuploadglue.onChange"));
		// options used by change callback to communicate with wicket
		options.put(OPTIONS_ON_CHANGE_CALLBACK, getOnChangeCallbackScript().toString());
		options.put(OPTIONS_ON_UPLOAD_FAILS_CALLBACK, getOnUploadFailsCallbackScript().toString());
		options.put(OPTIONS_ON_UPLOAD_DONE_CALLBACK, getOnUploadDoneCallbackScript().toString());
		options.put(OPTIONS_AUTO_UPLOAD, false);
		options.put(OPTIONS_URL, new LiteralOption(getFileUploadUrl(), true).toString());
		options.put(OPTIONS_PARAM_NAME, new LiteralOption(PARAMETERS_FILE_UPLOAD, true).toString());
		// la fonction génère une fonction de callback ciblant le progresscomponent indiqué
		options.put(OPTIONS_PROGRESSALL, new JsStatement().$().chain("fileuploadglue.progressallCallbackGenerator", JsUtils.doubleQuotes(progressComponent.getMarkupId(), true)).render(false).toString());
		return new JsStatement().$(component).chain("fileupload", options.getJavaScriptOptions());
	}

	/**
	 * Component head participation.
	 */
	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		response.render(JavaScriptHeaderItem.forReference(FileUploadGlueJavaScriptResourceReference.get()));
		response.render(OnDomReadyHeaderItem.forScript(statement(component).render()));
	}

	public static List<FileApiFile> readSuccessFileApiFiles(IRequestParameters parameters) throws JsonProcessingException, IOException {
		return OBJECT_MAPPER.readerFor(OBJECT_MAPPER.getTypeFactory().constructParametrizedType(List.class, List.class, FileApiFile.class)).readValue(parameters.getParameterValue(PARAMETERS_UPLOAD_DONE_SUCCESS_FILE_LIST).toString());
	}

	public static List<FileApiFile> readErrorFileApiFiles(IRequestParameters parameters) throws JsonProcessingException, IOException {
		return OBJECT_MAPPER.readerFor(OBJECT_MAPPER.getTypeFactory().constructParametrizedType(List.class, List.class, FileApiFile.class)).readValue(parameters.getParameterValue(PARAMETERS_UPLOAD_DONE_ERROR_FILE_LIST).toString());
	}

	public static List<FileApiFile> readFileApiFiles(IRequestParameters parameters) throws JsonProcessingException, IOException {
		return OBJECT_MAPPER.readerFor(OBJECT_MAPPER.getTypeFactory().constructParametrizedType(List.class, List.class, FileApiFile.class)).readValue(parameters.getParameterValue(PARAMETERS_FILE_LIST).toString());
	}

	public static String writeFileApiFiles(List<FileApiFile> files) throws JsonProcessingException, IOException {
		return OBJECT_MAPPER.writeValueAsString(files);
	}

}
