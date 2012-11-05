/**
 * Handlers
 */
GOKbExtension.handlers.suggest = function() {
	// Merge the meta-data and columns together.
	var params = $.extend({}, theProject.metadata,{
  	columns : theProject.columnModel.columns,
  });
	
  // Post the columns to the service
  GOKbExtension.doCommand (
    "describe",
    params,
    {
    	onDone : function (data) {
    		
    		// Create HTML list of returned items.
    		var list = $("<ul></ul>");
    		$.each(data.result, function() {
    			$("<li>")
    				.text(this.name)
    			.appendTo(list);
    		});
    		
    		// Create and show a dialog with the returned list attached.
    		var dialog = GOKbExtension.createDialog("Suggested Transformations", "suggest");
    		list.appendTo(dialog.bindings.dialogContent);
    		GOKbExtension.showDialog(dialog);
    	}
  	}
  );
//  dialog.bindings.dialogContent.html(GOKbExtension.server.suggest);
//  dialog.bindings.dialogContent.html(JSON.stringify(theProject.columnModel.columns));
};