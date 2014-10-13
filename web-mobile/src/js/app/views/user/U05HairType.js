// @formatter:off
define([
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/header/CommonHeader',
    'app/components/user/HairTypeComponent'
], function(IScrollContainer, ViewBase, SaveableHeader, HairTypeComponent) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var U05HairType = function(dom) {
        U05HairType.superclass.constructor.apply(this, arguments);

        var header = new CommonHeader($('<div/>').appendTo(this._dom$), {
            'title' : '设置',
            'right' : '保存'
        });

        header.on('clickRight', function(event) {
            // TODO
            // DataService.request('/user/update', main.save(), function() {
                // appRuntime.view.back();
            // });
        });

        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var main = new HairTypeComponent($('<div/>'));
        body.append(main);
    };
    andrea.oo.extend(U05HairType, ViewBase);

    return U05HairType;
});
