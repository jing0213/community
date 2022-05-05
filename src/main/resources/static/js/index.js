$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	var title = $('#recipient-name').val();
	var content = $('#message-text').val();
	$.post(
		CONTEXT_PATH+"/discuss/add",
		{"title":title,"content":content},
		function (data){
			data = $.parseJSON(data)

			$('#hintModal').text(data.msg);
			//显示提示框
			$("#hintModal").modal("show");
			console.log(data)
			//2s后，隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				if(data.code == 0){
					window.location.reload();
				}
			}, 2000);
		}
	);

}