from mitmproxy import ctx

js_prefoix = "<script type = 'text/javascript'> "
js_suffix = " </script> \n"
# Load in the javascript to inject.
with open('C:\Users\rober\Documents\firstpaper\keyboard-crawler\src\main\resources\mitmproxy-5.3.0\clearEventListenerOverride2.js', 'r') as f:
    injected_javascript = f.read()

def response(flow):
	for k in flow.response.headers.items():
		if "Content-Type" in str(k) or "content-type" in str(k):
			if "text/html" in str(k):
				html = flow.response.text
				headtag_open_index = html.find('<head') + 5
				string_after_headtag_open = html[headtag_open_index:]
				headtag_close_index_in__string_after_headtag_open = string_after_headtag_open.find('>')
				headtag_close_index = headtag_close_index_in__string_after_headtag_open + headtag_open_index + 1
				injected_html = html[:headtag_close_index] + js_prefoix + injected_javascript + js_suffix + html[headtag_close_index:]
				flow.response.text = injected_html
