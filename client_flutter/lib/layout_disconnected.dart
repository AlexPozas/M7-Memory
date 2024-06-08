import 'package:animated_rotation/animated_rotation.dart' as animated_rotation;
import 'package:client_flutter/layout_ranking.dart';
import 'package:flutter/cupertino.dart';
import 'package:provider/provider.dart';
import 'app_data.dart';

class LayoutDisconnected extends StatefulWidget {
  const LayoutDisconnected({super.key});

  @override
  State<LayoutDisconnected> createState() => _LayoutDisconnectedState();
}

class _LayoutDisconnectedState extends State<LayoutDisconnected> {
  final _ipController = TextEditingController();
  final _portController = TextEditingController();
  final _name = TextEditingController();

  Widget _buildTextFormField(
    String label,
    String defaultValue,
    TextEditingController controller,
  ) {
    controller.text = defaultValue;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Text(
          label,
          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w200),
        ),
        Container(
          constraints: const BoxConstraints(maxWidth: 200),
          child: CupertinoTextField(controller: controller),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    AppData appData = Provider.of<AppData>(context);
    String name = appData.defaultNames[appData.r.nextInt(appData.defaultNames.length)];
    double turn = 0;
    _ipController.text = appData.ip;

    return CupertinoPageScaffold(
      navigationBar: CupertinoNavigationBar(
        middle: Text("Lobby"),
        leading: GestureDetector(
          child: Container(
            height: 44,
            width: 44,
            child: Icon(CupertinoIcons.chart_bar_alt_fill, size: 24,)
          ),
          onTap: () async {
            appData.ranking = await appData.readFile("rankingFile.txt") as List<String>;
            Navigator.of(context).push(CupertinoPageRoute(builder: ((context) => Ranking())));
          },
        ),
      ),
      child: ListView(
        padding: const EdgeInsets.all(20),
        children: [
          const SizedBox(height: 50),
          _buildTextFormField("Server IP", appData.ip, _ipController),
          const SizedBox(height: 20),
          _buildTextFormField("Server port", appData.port, _portController),
          const SizedBox(height: 20),
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              _buildTextFormField("Name", name, _name),
              SizedBox(width: 10,),
              Column(
                children: [
                  SizedBox(height: 20,),
                  GestureDetector(
                    child: animated_rotation.AnimatedRotation(
                      angle: turn,
                      duration: Duration(seconds: 2),
                      child: Icon(CupertinoIcons.arrow_2_circlepath)
                    ),
                    onTap: () {
                      setState(() {
                        turn += 45;
                        name = appData.defaultNames[appData.r.nextInt(appData.defaultNames.length)];
                      });
                    },
                  ),
                ],
              )
              
            ],
          ),
          const SizedBox(height: 20),
          Row(mainAxisAlignment: MainAxisAlignment.center, children: [
            SizedBox(
              width: 96,
              height: 32,
              child: CupertinoButton.filled(
                onPressed: () async {
                  appData.restart();
                  appData.ip = _ipController.text;
                  appData.port = _portController.text;
                  appData.name = _name.text;
                  appData.playersName = [];
                  appData.playersName.add(_name.text);
                  appData.connectToServer();
                  appData.ranking = await appData.readFile("rankingFile.txt") as List<String>;
                },
                padding: EdgeInsets.zero,
                child: const Text(
                  "Connect",
                  style: TextStyle(
                    fontSize: 14,
                  ),
                ),
              ),
            ),
          ]),
          const SizedBox(height: 20),
        ],
      ),
    );
  }
}
