App.module('Vitalis.Models', function (Models, App, Backbone, Marionette, $, _) {

    var Urls    = App.module('Urls');

    function fetch(args, options){
        args = args || {};

        var onError = args.error || function(model, response, options){};

        args.beforeSend = function(xhr){
            xhr.setRequestHeader('X-Auth-Token', localStorage.getItem('accesstoken'));
        };

        args.error = function(model, response, options){
            if(response.status === 401){
                Urls.go('vitalis:login')
            } else {
                onError(model, response, options);
            }
        };

        return Backbone.Model.prototype.fetch.call(this, args, options);
    }

    Models.AbstractModel = Backbone.Model.extend({
        fetch: fetch
    });

    Models.AbstractCollection = Backbone.Collection.extend({
        fetch: fetch
    });

});