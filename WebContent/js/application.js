$(document).ready(function($) { 
	$('ul.menu, ul#children, ul.sub-menu').superfish({ 
		delay:       100,								// 0.1 second delay on mouseout 
		animation:   {opacity:'show',height:'show'},	// fade-in and slide-down animation 
		dropShadows: false								// disable drop shadows 
	});
});

$(document).ready(function() {
	// Create the dropdown base
   $("<select />").appendTo("#navigation");
      // Create default option "Go to..."
      $("<option />", {
         "selected": "selected",
         "value"   : "",
         "text"    : "跳转至..."
      }).appendTo("#navigation select");
      // Populate dropdown with menu items
      $("#navigation > ul > li:not([data-toggle])").each(function() {
      	var el = $(this);
      	var hasChildren = el.find("ul"),
      	    children    = el.find("li > a");
      	if (hasChildren.length) {
      		$("<optgroup />", {
      			"label": el.find("> a").text()
      		}).appendTo("#navigation select");
      		
      		children.each(function() {
      			$("<option />", {
					"value"   : $(this).attr("href"),
      				"text": " - " + $(this).text()
      			}).appendTo("optgroup:last");
      		});
      	} else {
      		$("<option />", {
	           "value"   : el.find("> a").attr("href"),
	           "text"    : el.find("> a").text()
	       }).appendTo("#navigation select");
      	} 
      });
 
      $("#navigation select").change(function() {
        window.location = $(this).find("option:selected").val();
      });
	//END -- Menus to <SELECT>	
}); //END -- JQUERY document.ready

// Tabs
jQuery(document).ready(function($){
	$('.stabs a').click(function(){
		switch_tabs($(this));
	});
	switch_tabs($('.defaulttab'));
	function switch_tabs(obj) {
		$('.stab-content').hide();
		$('.stabs a').removeClass("selected");
		var id = obj.attr("rel");
		$('#'+id).show();
		obj.addClass("selected");
	}
});

jQuery(document).ready(function(){
	jQuery('.inside ul li:last-child').css('border-bottom','0px'); // remove last border-bottom from list in tab conten
	jQuery('.tabs').each(function(){
		jQuery(this).children('li').children('a:first').addClass('selected'); // Add .selected class to first tab on load
	});
	jQuery('.inside > *').hide();
	jQuery('.inside > *:first-child').show();
   
	jQuery('.tabs li a').click(function(evt){ // Init Click funtion on Tabs
        var clicked_tab_ref = jQuery(this).attr('href'); // Strore Href value
        jQuery(this).parent().parent().children('li').children('a').removeClass('selected'); //Remove selected from all tabs
        jQuery(this).addClass('selected');
        jQuery(this).parent().parent().parent().children('.inside').children('*').hide();
        jQuery('.inside ' + clicked_tab_ref).fadeIn(500);
        evt.preventDefault();
	});
});

// Scroll to Top script
var $backToTopTxt = "返回顶部", $backToTopEle = $(
		'<div class="backToTop"></div>').appendTo($("body")).text(
		$backToTopTxt).attr("title", $backToTopTxt).click(function() {
	$("html, body").animate({
		scrollTop : 0
	}, 120);
}), $backToTopFun = function() {
	var st = $(document).scrollTop(), winh = $(window).height();
	(st > 0) ? $backToTopEle.show() : $backToTopEle.hide();
	if (!window.XMLHttpRequest) {
		$backToTopEle.css("top", st + winh - 166);
	}
};
$(window).bind("scroll", $backToTopFun);
$(function() {
	$backToTopFun();
});

// load qrcode
$(function(){
	var html = '<div id="qrcode" style="z-index: 999;cursor: pointer; position: fixed; bottom: 30px; left: 10px;"><img style="border: 2px;" width="100" height="100" src="' + cdn_path + '/api/qrcode-' +  qrcode + '.png" alt="扫描二维码即可访问本页" title="扫描二维码即可访问本页"></div>';
	setTimeout(function() {
	  return $('body').append(html);
	}, 500);
});
