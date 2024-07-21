console.log("begin");
var _listeners = [];

EventTarget.prototype.addEventListenerBase = EventTarget.prototype.addEventListener;
EventTarget.prototype.addEventListener = function(type, listener)
{
    _listeners.push({target: this, type: type, listener: listener});
    this.addEventListenerBase(type, listener);
};

EventTarget.prototype.removeEventListeners = function(targetType)
{
    for(var index = 0; index != _listeners.length; index++)
    {
        var item = _listeners[index];

        var target = item.target;
        var type = item.type;
        var listener = item.listener;

        if(target == this && type == targetType)
        {
            this.removeEventListener(type, listener);
        }
    }
}

// (function()
// {
//     let target = EventTarget.prototype;
//     let functionName = 'addEventListener';
//     let func = target[functionName];

//     let symbolHidden = Symbol('hidden');

//     function hidden(instance)
//     {
//         if(instance[symbolHidden] === undefined)
//         {
//             let area = {};
//             instance[symbolHidden] = area;
//             return area;
//         }

//         return instance[symbolHidden];
//     }

//     function listenersFrom(instance)
//     {
//         let area = hidden(instance);
//         if(!area.listeners) { area.listeners = []; }
//         return area.listeners;
//     }

//     target[functionName] = function(type, listener)
//     {
//         let listeners = listenersFrom(this);

//         listeners.push({ type, listener });

//         func.apply(this, [type, listener]);
//     };

//     target['removeEventListeners'] = function(targetType)
//     {
//         let self = this;

//         let listeners = listenersFrom(this);
//         let removed = [];

//         listeners.forEach(item =>
//         {
//             let type = item.type;
//             let listener = item.listener;

//             if(type == targetType)
//             {
//                 self.removeEventListener(type, listener);
//             }
//         });
//     };
// })();
console.log("Element.removeEventListener overridden with injected script v2");