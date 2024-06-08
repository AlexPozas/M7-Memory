import 'package:client_flutter/widgets/scorePanel.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/rendering.dart';
import 'package:provider/provider.dart';
import 'package:screenshot/screenshot.dart';
import 'app_data.dart';

class LayoutConnected extends StatefulWidget {
  const LayoutConnected({Key? key}) : super(key: key);

  @override
  State<LayoutConnected> createState() => _LayoutConnectedState();
}

class _LayoutConnectedState extends State<LayoutConnected> {
  final ScrollController _scrollController = ScrollController();
  final _messageController = TextEditingController();
  final FocusNode _messageFocusNode = FocusNode();

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    AppData appData = Provider.of<AppData>(context);
    double screenWidth = MediaQuery.of(context).size.width;
    double screenHeight = MediaQuery.of(context).size.height;

    return CupertinoPageScaffold(
      navigationBar: CupertinoNavigationBar(
        padding: EdgeInsetsDirectional.all(4),
        leading: GestureDetector(
          child: Container(
            height: 44,
            width: 44,
            child: Icon(CupertinoIcons.back, color: CupertinoColors.activeBlue, size: 24,),
            decoration: BoxDecoration(
              color: CupertinoColors.white,
            ),
          ),
          onTap: () {
            appData.disconnectFromServer();
          },
        ),
        middle: Text(
          'Memory',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
      ),
      child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            SizedBox(
              height: 50,
            ),
            Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                scorePanel(appData.playersName[0],
                    appData.playersScore[0].toString(), appData.meActivePlayer, true),
                scorePanel(appData.playersName[1],
                    appData.playersScore[1].toString(), !appData.meActivePlayer, false)
              ],
            ),
            Container(
              constraints: BoxConstraints(
                maxWidth: 500,
                maxHeight: 500,
              ),
              child: Screenshot(
                controller: appData.screenshotController,
                child: _tablero(appData)
              ),
            )
          ]),
    );
  }

  GridView _tablero(AppData appData) {
    return GridView.builder(
      itemCount: appData.gameImages!.length,
      gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 4,
          crossAxisSpacing: 16,
          mainAxisSpacing: 16,
      ),
      padding: EdgeInsets.all(16),
      itemBuilder: (context, index) {
        return GestureDetector(
          onTap: () {
            setState(() {
              appData.boldCard(index);
            });
          },
          child: Container(
              padding: EdgeInsets.all(16),
              decoration: BoxDecoration(
                  borderRadius:
                      BorderRadius.all(Radius.circular(8)),
                  gradient: RadialGradient(colors: [CupertinoColors.activeBlue, CupertinoColors.systemCyan]),
                  image: DecorationImage(
                      image: AssetImage(appData.gameImages![index]),
                      fit: BoxFit.cover)
              )
          ),
        );
      });
  }

}
