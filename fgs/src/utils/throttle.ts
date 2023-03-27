/**
 * 节流
 * @reference https://stackoverflow.com/questions/27078285/simple-throttle-in-javascript
 * @param {Function} callback 
 * @param {Number} limit 
 * @returns Function
 */
export function throttle(callback: Function, limit: number) {
    let obj: any;
    var waiting = false;                         // Initially, we're not waiting
    return function (this: any) {                // We return a throttled function
        if (!waiting) {                          // If we're not waiting
            obj = this                           // For bind this in setTimeout
            waiting = true;                      // Prevent future invocations
            setTimeout(function () {             // After a period of time
                callback.apply(obj, arguments);  // Execute users function
                waiting = false;                 // And allow future invocations
            }, limit);
        }
    }
}