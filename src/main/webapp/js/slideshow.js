/**
 * 
 */

$(function (){
	init();
});

function init(){
	checkLink();
}

var slideUrl = "";

function checkLink(){
	$.ajax("service/slidestate")
	.done(function(result){
		console.log(result);
		if($.trim(result)!=slideUrl){
			$("#slideShowFrameId").attr('src',result);
			slideUrl = $.trim(result);
		} 
		result = null;
		$.wait(2000).then(checkLink);
	});
}

$.wait = function(ms) {
    var defer = $.Deferred();
    setTimeout(function() { defer.resolve(); }, ms);
    return defer;
};