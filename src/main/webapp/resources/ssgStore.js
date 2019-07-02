var SsgStore = (function() {
	var storeV = {}; // Volatile
	var storeNV = {}; // Non-Volatile
	var hash = "#ajaxmore";
	
	var getValue = function(store, key) {
		var Storage = (store == storeNV) ? localStorage : sessionStorage;
		var expireTime = Storage.getItem("expireTime" + key);
		
		if(null == expireTime) {
			return {};
		}
		
		if(key.indexOf(hash) >= 0 && hash != location.hash
				|| new Date().getTime() > parseInt(expireTime)) {
			Storage.removeItem(key);
			Storage.removeItem("expireTime" + key);
			return {};
		}
		
		return JSON.parse(Storage.getItem(key)) || {};
	}

	var loadStore = function(store) {
		if(store != storeV && store != storeNV) return;
		
		var key = location.pathname + location.search;
		
		store[key] = getValue(store, key);
		store[key + hash] = getValue(store, key + hash);
	}

	var render = function(store) {
		$.each(store, function(key, value) {
			if(store.hasOwnProperty(key)) {
				$.each(value, function(domId, html) {
					if(value.hasOwnProperty(domId)) {
						$("#" + domId).html(html);
					}
				});
			}
		});
	}

	var fetch = function() {
		if(!sessionStorage || !localStorage) return;

		loadStore(storeV);
		render(storeV);
		loadStore(storeNV);
		render(storeNV);
	}
	
	var saveStore = function(key, store, ttl) {
		if(store != storeV && store != storeNV) return;
		var Storage = (store == storeNV) ? localStorage : sessionStorage;
		
		ttl = ttl || (store == storeNV) ? 24*60*60*1000 : 60*60*1000;
        
        try {
            Storage.setItem(key, JSON.stringify(store[key]));
            Storage.setItem("expireTime" + key, (new Date().getTime() + ttl).toString());
        } catch(e) {
        }
	}

	var commit = function(domId, html, option) {
		if(!sessionStorage || !localStorage) return;
		domId = domId || "", html = html || "", option = option || {};

		var key = location.pathname + location.search + ((false === option.hash) ? "" : hash);
		var store = (false === option.volatile) ? storeNV : storeV;
		var ttl = $.isNumeric(option.ttl) ? option.ttl * 1000 : 0;
		
		store[key][domId] = html;
		saveStore(key, store, ttl);
		location.hash = hash;
	}

	$(function() {
		fetch();
	});
	
	return {
		"commit" : commit
	}
}());