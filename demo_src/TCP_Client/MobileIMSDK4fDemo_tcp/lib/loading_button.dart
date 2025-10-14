import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

/// 有加载动画的按钮
class LoadingButton extends StatefulWidget {
  final bool loading;
  final String title;
  final String loadingText;
  final TextStyle? titleStyle;
  final Widget? indicator;
  final Color? loadingColor;
  final Color? color;
  final double radius;
  final double width;
  final double height;
  final VoidCallback? onTap;

  const LoadingButton({
    super.key,
    this.loading = false,
    required this.title,
    this.loadingText = '加载中',
    this.titleStyle,
    this.loadingColor,
    this.color,
    this.radius = 0,
    this.height = 30,
    this.width = 100,
    this.indicator,
    this.onTap,
  });

  @override
  State<LoadingButton> createState() => _LoadingButtonState();
}

class _LoadingButtonState extends State<LoadingButton> {
  @override
  void initState() {
    super.initState();
  }

  /// 标题
  Text _titleWidget() => Text(
        widget.loading ? widget.loadingText : widget.title,
        style: widget.titleStyle ??
            const TextStyle(
              fontSize: 16,
              color: Colors.white,
            ),
        overflow: TextOverflow.ellipsis,
      );

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        if (!widget.loading && widget.onTap != null) {
          widget.onTap!();
        }
      },
      child: Container(
        decoration: BoxDecoration(
          color: widget.color ?? Colors.blue,
          borderRadius: BorderRadius.all(Radius.circular(widget.radius)),
        ),
        child: TextButton(
          onPressed: () {
            if (!widget.loading && widget.onTap != null) {
              widget.onTap!();
            }
          },
          child: widget.loading
              ? Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    widget.indicator ??
                        CupertinoActivityIndicator(
                          animating: widget.loading,
                          color: widget.loadingColor ?? Colors.white,
                        ),
                    const SizedBox(width: 8),
                    _titleWidget(),
                  ],
                )
              : _titleWidget(),
        ),
      ),
    );
  }
}
