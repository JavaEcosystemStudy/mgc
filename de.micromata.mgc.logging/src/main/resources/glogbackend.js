
function LogDummyBackend(logViewer) {
	this.supportsPoll = false;
	this.supportsSearch = false;
	this.loggingConfiguration = {
		supportSearch: false,
		supportsFulltextSearch: false,
		loggingCategories: [],
		attributes: [],
		searchAttributes: [],
		threshold: 'Debug'
	};
	this.init = function(logViewer) {

	}
	this.logPoll = function(lastPollTime, callback) {
		callback([]);
	};
	this.logSelect = function(logFormData, callback) {

	}
}

function GLogBackend() {

	
	var _this = this;
	
	this.init = function(logViewer) {

		this.logViewer = logViewer;
		this.backendUrl = logViewer.options.backendUrl;
		this.supportsSearch = null;
		this._initLogConfig();
	}

	this.logPoll = function(lastPollTime, callback) {
		this._ajax("poll", 'lt=' + lastPollTime, callback);
	};
	
	this.logSelect = function(logFormData, callback) {
		// is type of GLogFormData
		var urlp = '';
		for (var key in logFormData) {
			var value = logFormData[key];
			if (!value) {
				continue;
			}
			if (urlp.length > 0) {
				urlp += '&';
			}
			urlp += encodeURIComponent(key) + "=" + encodeURIComponent(value);
		}
		this._ajax("search", urlp, function(text) {
			var res = JSON.parse(text);
			callback(res);
		});
	};
	
	this._initLogConfig = function()
	{
		this._ajax("getConfiguration", null, function(text) {
			var res = JSON.parse(text);
			_this.supportsSearch = res.supportsSearch;
			_this.loggingConfiguration = res;
		});
	}
	this._ajax = function(cmd, data, callback)
	{
		var xmlhttp = new XMLHttpRequest();
		var url = this.backendUrl + "?cmd=" + cmd;
		if (data) {
			url += '&' + data;
		}
		xmlhttp.open("POST", url, true);
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				callback(xmlhttp.responseText);
			}
		};
		xmlhttp.send();
	}
	
}