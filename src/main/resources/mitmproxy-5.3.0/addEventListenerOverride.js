/*
(function() {
    Element.prototype._addEventListener = Element.prototype.addEventListener;
    Element.prototype.addEventListener = function(a,b,c) {
      this._addEventListener(a,b,c);
      if(!this.eventListenerList) this.eventListenerList = {};
      if(!this.eventListenerList[a]) this.eventListenerList[a] = [];
      this.eventListenerList[a].push(b);
    };
  })();
console.log("Element.addEventListener overridden with injected script");
*/


// new with event flow info
(function() {
  Element.prototype._addEventListener = Element.prototype.addEventListener;
  Element.prototype.addEventListener = function(a,b,c) {
    this._addEventListener(a,b,c);
    if(!this.eventListenerList) this.eventListenerList = {};
    if(!this.eventListenerList[a]) this.eventListenerList[a] = []; //type
    clickEventProperties = {}
    clickEventProperties.handler = b//[]  // listener
    //clickEventProperties['listener'].push(b)
    clickEventProperties.useCapture = c//[]  // useCapture
    //clickEventProperties['useCapture'].push(c)
    //this.eventListenerList[a].push(b);
    this.eventListenerList[a].push(clickEventProperties);
  };
})();
console.log("Element.addEventListener overridden with injected script");