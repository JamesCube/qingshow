// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, DataService, RenderUtils) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Show = function(dom, data) {
        Show.superclass.constructor.apply(this, arguments);
        this._show = data;

        async.parallel([ function(callback) {
            // Load template
            TemplateManager.load('show/show.html', function(err, content$) {
                this._dom$.append(content$);
                callback(null);
            }.bind(this));
        }.bind(this)], function(err, results) {
            this._render();
        }.bind(this));

    };
    andrea.oo.extend(Show, UIComponent);

    Show.prototype._render = function() {
        Show.superclass._render.apply(this, arguments);

        var show = this._show;
        // Video
        var video$ = $('.qsVideo', this._dom$);
        $('<source/>').attr({
            'type' : 'video/mp4',
            'src' : RenderUtils.videoPathToURL(show.video)
        }).appendTo(video$);
        var vjs = videojs(video$.get(0));

        var videoPostersContainer$ = $('.qsSlickVideoPosters', this._dom$), slickItemTplt$;
        show.posters.forEach(function(poster, index) {
            var slickItem$;
            if (index === 0) {
                slickItem$ = slickItemTplt$ = $('.clone', videoPostersContainer$);
            } else {
                slickItem$ = slickItemTplt$.clone().appendTo(videoPostersContainer$);
            }
            $('.qsItemCover', slickItem$).css('background-image', RenderUtils.videoPathToBackground(poster));
        });
        videoPostersContainer$.slick({
            'dots' : true,
            'arrows' : false,
            'slidesToShow' : 1,
            'slidesToScroll' : 1
        });

        var play$ = $('.qsVideoPlay', this._dom$);
        vjs.on('play', function() {
            videoPostersContainer$.hide().off();
            play$.hide();
        });
        vjs.on('pause', function() {
            play$.show();
        });
        // vjs.on('ended', function() {
            // vjs.currentTime(0);
        // });
        videoPostersContainer$.on('click', vjs.play.bind(vjs));
        // Model
        $('.qsPortrait', this._dom$).css('background-image', RenderUtils.imagePathToBackground(show.producerRef.portrait));
        $('.qsName', this._dom$).text(show.producerRef.name);
        $('.qsAge', this._dom$).text(RenderUtils.timeToAge(show.producerRef.birthtime) + '岁');
        $('.qsStatus', this._dom$).text(show.producerRef.modelInfo.status);
        $('.qsNumFollowers', this._dom$).text(show.numLike);

        // Items
        var itemCoversContainer$ = $('.qsSlickItemCovers', this._dom$), slickItemTplt$;
        show.itemRefs.forEach(function(item, index) {
            var slickItem$;
            if (index === 0) {
                slickItem$ = slickItemTplt$ = $('.clone', itemCoversContainer$);
            } else {
                slickItem$ = slickItemTplt$.clone().appendTo(itemCoversContainer$);
            }
            $('.qsItemCover', slickItem$).css('background-image', RenderUtils.imagePathToBackground(item.cover));
        });
        itemCoversContainer$.slick({
            'dots' : true,
            'arrows' : false,
            'slidesToShow' : 3,
            'slidesToScroll' : 1
        });

        var itemDescriptionContainer$ = $('.qsItemDescriptions', this._dom$), categoryTplt$, nameTplt$;
        show.itemRefs.forEach(function(item, index) {
            var category$, name$;
            if (index === 0) {
                category$ = categoryTplt$ = $('.clone', itemDescriptionContainer$).eq(0);
                name$ = nameTplt$ = $('.clone', itemDescriptionContainer$).eq(1);
            } else {
                category$ = categoryTplt$.clone().appendTo(itemDescriptionContainer$);
                name$ = nameTplt$.clone().appendTo(itemDescriptionContainer$);
            }
            category$.text(item.category);
            name$.text(item.name);
        });
    };

    return Show;
});
