function getMoreContents(btnEl, domId) {
	if($('#pnshop_more_loading').is(":visible")) return;

	var domEl = $("#" + domId);
	var page = $(btnEl).data('listPage');

	var param = '';
	if(location.search) {
		param = location.search + '&page=' + page;
	} else {
		param = '?page=' + page;
	}

	$('#pnshop_more_loading').show();
	$('.ssgmain_theme_more').hide();

	$.ajax({
		url: '/main/ajaxGetMoreContents.ssg' + param,
		method: 'GET',
		dataType: 'html'
	}).done(function (data) {
		
		$('#pnshop_more_loading').hide();
		
		domEl.append(data);
		domEl.append($('.ssgmain_theme_more'));

		$(btnEl).data("listPage", page + 1)
				.attr("data-list-page", page + 1);
		if(true === domEl.find('.ssgmain_subject').last().data('hasNext')) {
			$('.ssgmain_theme_more').show();
		}

		Clip.readyClipBtn();
		SsgStore.commit(domId, domEl.html());
	});
}



ssg.ViewModel.howdy.ItemMore = (function () {
    var hash = "#ajaxmore";

    function ItemMore(param) {
        param = param || {};

        var _$init = function() {
            if(hash == location.hash) {
                $(function(){
                    initInfiniteGrid($(param.wrapper + " " + param.btn + " a.btn_more"));
                });
            } else {
                $(param.wrapper).on("click", param.btn + " a.btn_more", function(e) {
                    initInfiniteGrid($(e.currentTarget));
                });
            }
        };

        _$init();
    }

    var initInfiniteGrid = function(btnEl) {
		location.hash = hash;
        btnEl.hide();
        var oInfiniteGrid = new ssg.Common.infiniteGrid({nEndPage: 20});

        oInfiniteGrid.on('fetchItems', function(nCurPage) {
            var oSelf = this;
            ajaxItemMore(nCurPage)
            .done(function(data) {
                var html = $($.parseHTML(data)).filter(".unit");
                oSelf.addItemList(html);
            })
            .fail(function() {
                oSelf.finalData();
            });
        });

        //뒤로가기
        var oInfinitePersist = oInfiniteGrid.oPersist;
        if (oInfinitePersist.get('') !== null) {
            oInfiniteGrid.historyBack();
        } else {
            oInfiniteGrid.firstRender();
        }
    }

    var ajaxItemMore = function(nCurPage) {
        return $.ajax({
            url: '/main/ajaxMainItemMore.ssg?page=' + nCurPage,
            method: 'GET',
            dataType: 'html'
        });
    }

    return ItemMore;
}());
new ssg.ViewModel.howdy.ItemMore({wrapper: ".hwd_newarrival_wrap", btn: "#new_arrivals_btn"});

ssg.ViewModel.preorder.ItemMore = (function () {
    var ajaxMoreitem = { state: function() {} };

    function ItemMore(param) {
        param = param || {};

        // TODO 페이지 속성 추가 // this.page = 1;
        this.type = param.type;
        this.totalPage = $(param.wrapper).find("ul").data("totalPage"); // number type

        var _$init = function() {
            $(function(){
                ssgPreOrder = new ssg.View.preOrder(); // global
            });
            $(param.wrapper).on("click", param.btn + " button", function(e) {
                var moreitemBtnEle = $(e.currentTarget).closest(param.btn);
                var ulItemEle = $(e.currentTarget).closest(param.wrapper).find(param.ul);

                fetchMoreitem.call(this, moreitemBtnEle, ulItemEle);
            }.bind(this));
        }.bind(this);

        _$init();
    }

    var fetchMoreitem = function(moreitemBtnEle, ulItemEle) {
        if('pending' === ajaxMoreitem.state()) { return; }

        ajaxItemMore.call(this)
        .done(function(data) {
            var html = $($.parseHTML(data)).filter("li");
            // TODO 엔드페이지 체크 if(this.page ==  this.totalPage(
            moreitemBtnEle.hide();
            renderMoreitem(ulItemEle, html);
            updateMoreitem(ulItemEle);
        }.bind(this))
        .fail(function() {
            moreitemBtnEle.hide();
        })
        .always(function() {
            ssgPreOrder.mpoderComingsoonList.masonry('reloadItems').masonry(); // global
        });
    }

    var renderMoreitem = function(ulItemEle, html) {
        ulItemEle.append(html);
    };

    var updateMoreitem = function(ulItemEle) {
        var $img = ulItemEle.find('img');
        var totalImg = $img.length;

        var waitImgLoad = function() {
            if(!--totalImg) { ssgPreOrder.mpoderComingsoonList.masonry('layout'); } // global
        }

        $img.each(function() {
            var oSelf = this;
            if(oSelf.complete) { waitImgLoad(); }
        });

        $img.load(waitImgLoad).error(waitImgLoad);
    }

    var ajaxItemMore = function() {
        ajaxMoreitem = $.ajax({
            url: '/service/preorder/ajaxItemMore.ssg?type=' + this.type, // TODO 페이지 파라미터 추가 ++this.page;
            method: 'GET',
            dataType: 'html'
        });

        return ajaxMoreitem;
    }

    return ItemMore;
}());
new ssg.ViewModel.preorder.ItemMore({wrapper: ".mpoder_closeprd", ul:".mpoder_closeprd_lst", btn: ".mpoder_btn_more", type: "closed"});
new ssg.ViewModel.preorder.ItemMore({wrapper: ".mpoder_comingsoon", ul:".mpoder_comingsoon_list", btn: ".mpoder_btn_more", type: "soon"});
