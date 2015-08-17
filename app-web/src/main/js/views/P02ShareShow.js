// @formatter:off
define([
], function(
) {
// @formatter:on
    var P02ShareShow = function(dom, initOptions) {
        P02ShareShow.superclass.constructor.apply(this, arguments);

        __services.httpService.request('/spread/open', 'get', {
            'entry' : violet.url.search.entity || "",
            'initiatorRef' : violet.url.search.initiatorRef || "",
            'targetRef' : violet.url.search.targetRef || ""
        }, function(err, metadata, data) {});

        __services.httpService.request('/show/query', 'get', {
            '_ids' : [initOptions._id]
        }, function(err, metadata, data) {
            if (data) {
                var show = data.shows[0];

                $('.p02-show-cover', this._dom).css('background-image', violet.string.substitute('url({0})', show.cover));
                $('.p02-show-cover-foreground', this._dom).css('background-image', violet.string.substitute('url({0})', show.coverForeground));
            }
        }.bind(this));

        $('.p02-download', this._dom).on('click', __services.downloadService.download);
        $('.p02-sale', this._dom).on('click', __services.downloadService.download);
    };
    violet.oo.extend(P02ShareShow, violet.ui.ViewBase);

    return P02ShareShow;
});
